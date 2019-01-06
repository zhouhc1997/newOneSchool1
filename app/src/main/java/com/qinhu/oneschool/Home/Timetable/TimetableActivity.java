package com.qinhu.oneschool.Home.Timetable;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qinhu.oneschool.MyClass.Anim;
import com.qinhu.oneschool.MyClass.BaseDialog;
import com.qinhu.oneschool.MyClass.PickerView;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.FileUtil;
import com.qinhu.oneschool.Utils.FormatTime;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.qinhu.oneschool.Utils.FileUtil.writeTxtToFile;
import static com.qinhu.oneschool.Utils.FormatTime.getDateOFWeek;

public class TimetableActivity extends AppCompatActivity {
    private ImageView backbtn;
    private LinearLayout setting;
    private TextView textView;
    private TextView dqxq;
    private ImageView weekImg;
    private LinearLayout backweek;
    private LinearLayout selectWeek;
    private LinearLayout weeklayout;
    private LinearLayout weekbox;
    private LinearLayout changeweek;
    private ImageView background;
    private Boolean flag = true;
    private int theWeek = 1;
    private int theCurrentWeek = 1;
    private int MaxWeek = 23;

    private String stuUsername;

    private BaseDialog myDialog;
    private BaseDialog dialog;
    private Boolean isShowNotCurrentWeek = true;
    private String[] projectname;

    private  String filePath = Environment.getExternalStorageDirectory().getPath()+"/oneschool/";
    private String fileName = "";

    private int projectnum = 0;
    private int[] colorArray = {R.color.tablegray,R.color.tablehong,R.color.tableorange,
            R.color.tableblue,R.color.tablezi,R.color.tablegreen,R.color.tablered,
            R.color.tablebluedark, R.color.tableorange,R.color.tablezi,R.color.tablegreen,
            R.color.tablebluedark, R.color.tableorange, R.color.tablezi,R.color.tablegreen,
            R.color.tablebluedark, R.color.tableorange,R.color.tablezi,R.color.tablegreen};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        StatusBarUtil.noColorBar(TimetableActivity.this);

            SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
            stuUsername = preferences.getString("stuUsername",null);
            fileName = stuUsername + ".json";

            initView();
            initEvent();
        }


    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeekLayout();
            }
        });
        changeweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(TimetableActivity.this,TimetableSettingActivity.class),1);
            }
        });
    }

    public void initView() {
        backbtn = findViewById(R.id.id_timetable_backbtn);
        backweek = findViewById(R.id.id_timetable_backweek);
        textView = findViewById(R.id.weekTextView);
        dqxq = findViewById(R.id.id_timetable_dqxq);
        setting = findViewById(R.id.id_timetable_setting);
        selectWeek = findViewById(R.id.id_timetable_selectweek);
        weeklayout = findViewById(R.id.id_timetable_weeklayout);
        weekbox = findViewById(R.id.id_timetable_weekbox);
        weekImg = findViewById(R.id.id_timetable_weekimg);
        changeweek = findViewById(R.id.id_timetable_changeweek);
        background = findViewById(R.id.id_timetable_background);

        selectDayOfWeek(true,0);

        String week = "week" + FormatTime.getWeekOfDate(new Date());
        String day = "day" + FormatTime.getWeekOfDate(new Date());
        Resources res=getResources();
        int x =res.getIdentifier(week,"id",getPackageName());
        int y =res.getIdentifier(day,"id",getPackageName());
        TextView w = findViewById(x);
        TextView d = findViewById(y);
        w.setTextColor(getResources().getColor(R.color.selected));
        d.setTextColor(getResources().getColor(R.color.selected));

        SharedPreferences prefer = getSharedPreferences("timetable" + stuUsername,MODE_PRIVATE);
        if (!prefer.contains("dqxq")){
            dqxq.setText("2018-2019-1");
        }else {
            dqxq.setText(prefer.getString("dqxq",null));
        }

        if (prefer.contains("background")){
            Glide.with(TimetableActivity.this)
                    .load(prefer.getString("background",null))
                    .into(background);
        }

        if (!prefer.contains("dqzc")||!prefer.contains("dqzcmonday")){
            theWeek = theCurrentWeek = 1;
        }else {
            int days = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(prefer.getString("dqzcmonday",null));
                days = FormatTime.differentDaysByMillisecond(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            theWeek = theCurrentWeek = Integer.parseInt(prefer.getString("dqzc",null)) + (days+1)/7 ;
        }
        textView.setText( "第" + theWeek + "周");
        textView.setTextColor(theWeek==theCurrentWeek?getResources().getColor(R.color.black):getResources().getColor(R.color.selected));

        if(getFileFromSD(filePath+fileName).isEmpty()){
            Toast.makeText(TimetableActivity.this,"请导入课表",Toast.LENGTH_SHORT).show();
        }else {
            show();
        }

        ShowWeekLayout();
    }

    private void openWeekLayout() {
        if (flag){
            Anim.start(TimetableActivity.this,300,Anim.ROTATE_UP,weekImg);
            mAnimator(true);
            ((HorizontalScrollView)findViewById(R.id.id_timetable_hs)).scrollTo(100*(theWeek-3),0);
            flag = false;
        }else {
            theWeek = theCurrentWeek;
            textView.setText( "第" + theWeek + "周");
            textView.setTextColor(theWeek==theCurrentWeek?getResources().getColor(R.color.black):getResources().getColor(R.color.selected));
            show();
            ShowWeekLayout();
            Anim.start(TimetableActivity.this,300,Anim.ROTATE_DOWN,weekImg);
            mAnimator(false);
            flag = true;
        }
    }

    private void selectDayOfWeek(Boolean flag,int n){
        for (int i = 0;i < 7;i++){
            String day = "day" + i;
            Resources res=getResources();
            int y =res.getIdentifier(day,"id",getPackageName());
            TextView d = findViewById(y);
            d.setText(getDateOFWeek(i,n));
        }

        String week = "week" + FormatTime.getWeekOfDate(new Date());
        String day = "day" + FormatTime.getWeekOfDate(new Date());
        Resources res=getResources();
        int x =res.getIdentifier(week,"id",getPackageName());
        int y =res.getIdentifier(day,"id",getPackageName());
        TextView w = findViewById(x);
        TextView d = findViewById(y);

        if (flag){
            w.setTextColor(getResources().getColor(R.color.selected));
            w.setBackgroundColor(getResources().getColor(R.color.blue));
            d.setTextColor(getResources().getColor(R.color.selected));
            d.setBackgroundColor(getResources().getColor(R.color.blue));
        }else {
            w.setTextColor(getResources().getColor(R.color.text_75));
            d.setTextColor(getResources().getColor(R.color.text_75));
            w.setBackgroundColor(getResources().getColor(R.color.noColor));
            d.setBackgroundColor(getResources().getColor(R.color.noColor));
        }
    }

    private void mAnimator(final Boolean flag){
        final ValueAnimator valueAnimator;
        if (flag){
            valueAnimator = ValueAnimator.ofInt(0,150);
        }else {
            valueAnimator = ValueAnimator.ofInt(150,0);
        }
        final View view =  ((View)findViewById(R.id.block));
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (Integer)valueAnimator.getAnimatedValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                view.getLayoutParams();
                layoutParams.height = curValue;
                view.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
    }

    private void ShowWeekLayout(){
        selectDayOfWeek(theWeek==theCurrentWeek,theWeek-theCurrentWeek);
        weeklayout.removeAllViews();
        for (int i = 1; i <= MaxWeek; i++){
            View view = RelativeLayout.inflate(TimetableActivity.this,R.layout.week_layout,null);
            ShapeCornerBgView text = view.findViewById(R.id.id_weeklayout_text);
            text.setText(i+"");

            if (i == theCurrentWeek){
                text.setBgColor(getResources().getColor(R.color.liner_eee));
                text.setTextColor(getResources().getColor(R.color.white));
            }
            if (i == theWeek){
                text.setBgColor(getResources().getColor(R.color.selected));
                text.setTextColor(getResources().getColor(R.color.white));
            }

            if (theWeek == theCurrentWeek){
                dqxq.setVisibility(View.VISIBLE);
                backweek.setVisibility(View.GONE);
            }else {
                dqxq.setVisibility(View.GONE);
                backweek.setVisibility(View.VISIBLE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShapeCornerBgView t= v.findViewById(R.id.id_weeklayout_text);
                    theWeek = Integer.parseInt(t.getText().toString());
                    textView.setTextColor(theWeek==theCurrentWeek?getResources().getColor(R.color.black):getResources().getColor(R.color.selected));
                    textView.setText( "第" + theWeek + "周");
                    show();
                    ShowWeekLayout();
                    selectDayOfWeek(theWeek==theCurrentWeek,theWeek-theCurrentWeek);
                }
            });
            weeklayout.addView(view);
        }
    }

    //设置当前周
    private void selectWeek() {
        View view = getLayoutInflater().inflate(R.layout.weekpicker, null);
        PickerView week = (PickerView) view.findViewById(R.id.id_pickerview_week);
        final String[] selectWeek = {""};
        List<String> WeekData = new ArrayList<String>();
        for (int i = 1;i <= 25;i++){
            WeekData.add("" + i);
        }
        week.setData(WeekData,theCurrentWeek-1);
        week.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectWeek[0] = text;
            }
        });
        dialog = new BaseDialog.Builder(this).setTitle("请选择当前周次")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        theCurrentWeek = selectWeek[0].isEmpty()?theCurrentWeek:Integer.parseInt(selectWeek[0]);
                        editor.putString("dqzc",theCurrentWeek + "");
                        editor.putString("dqzcmonday", FormatTime.getMondayOFWeek());
                        editor.commit();
                        initView();
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                }).setView(view).create();
        dialog.show();
    }

    private static String getFileFromSD(String path) {
        String result = "";
        try {
            FileInputStream f = new FileInputStream(path);
            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
            String line = "";
            while ((line = bis.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void show() {
        for (int j = 1; j <= 7; j++){
            String a = "id_timetable_week" + j;
            Resources res=getResources();
            int x =res.getIdentifier(a,"id",getPackageName());
            LinearLayout linearLayout = findViewById(x);
            linearLayout.removeAllViews();
        }

        SharedPreferences preferences = getSharedPreferences("timetable" + stuUsername,MODE_PRIVATE);
        if (!preferences.contains("isShowNotCurrentWeek")){
            isShowNotCurrentWeek = true;
        }else {
            isShowNotCurrentWeek = preferences.getBoolean("isShowNotCurrentWeek",false);
        }

        projectname = new String[20];
        projectnum = 0;

        try {
            JSONObject jsonObject = new JSONObject(getFileFromSD(filePath+fileName));
            for(int i = 1;i <= 7;i++){
                String day = jsonObject.getString(i+"");
                JSONObject object = new JSONObject(day);
                for(int j = 1;j <= 6;j++){
                    String info = object.getString(j+"");
                    JSONArray in = new JSONArray(info);

                    String a = "id_timetable_week" + i;
                    Resources res=getResources();
                    int x =res.getIdentifier(a,"id",getPackageName());
                    LinearLayout linearLayout = findViewById(x);
                    View view = LinearLayout.inflate(TimetableActivity.this,R.layout.tablebox,null);
                    TextView all = view.findViewById(R.id.id_tablebox_all);
                    ShapeCornerBgView t = view.findViewById(R.id.id_tablebox_text);
                    String showString = "";

                    final int week = i;
                    final int num  = j;

                    for(int m = 0,size = in.length();m<size;m++){
                        JSONObject object1 = in.getJSONObject(m);
                        if(object1.length() == 0)
                            continue;
                        String kcm = object1.getString("kcm");
                        String skdd = object1.getString("skdd");
                        String tname = object1.getString("tname");
                        String jxb = object1.getString("jxb");
                        String time = object1.getString("time");
                        showString = kcm + "@" + skdd;
                        if (iscontains(time,theWeek + "周")){
                            if (Arrays.asList(projectname).contains(kcm)){
                                for(int k = 0;k<projectname.length;k++){
                                    if(kcm.equals(projectname[k])){
                                        t.setBgColor(getResources().getColor(colorArray[k]));
                                    }
                                }
                            }else {
                                t.setBgColor(getResources().getColor(colorArray[projectnum+1]));
                                projectname[projectnum+1] = kcm;
                                projectnum = projectnum+1;
                            }
                            all.setText(week + "#" + num + "#" + kcm + "#" + skdd + "#" + tname + "#" + jxb  + "#" + time );
                            t.setTextColor(0xffffffff);
                            break;
                        }else {
                            if (isShowNotCurrentWeek) {
                                all.setText(week + "#" + num + "#" + kcm + "#" + skdd + "#" + tname + "#" + jxb  + "#" + time);
                                showString = "[非本周]" + showString;
                                t.setBgColor(getResources().getColor(colorArray[0]));
                                t.setTextColor(0xff999999);
                            } else {
                                all.setText(i + "#" + j);
                                t.setTextColor(0x00000000);
                            }
                        }
                    }
                    t.setText(showString.replace(" ",""));
                    t.setMaxLines(8);
                    view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TextView all = v.findViewById(R.id.id_tablebox_all);
                        String[] string = all.getText().toString().split("#");
                        final View view = getLayoutInflater().inflate(R.layout.kcxq_layout, null);

                        EditText kcmc = (EditText)view.findViewById(R.id.id_kcxqlayout_kcmc);
                        EditText skdd = (EditText)view.findViewById(R.id.id_kcxqlayout_skdd);
                        EditText skjs = (EditText)view.findViewById(R.id.id_kcxqlayout_skjs);
                        EditText sksj = (EditText)view.findViewById(R.id.id_kcxqlayout_sksj);
                        EditText kkzc = (EditText)view.findViewById(R.id.id_kcxqlayout_kkzc);
                        EditText ktbj = (EditText)view.findViewById(R.id.id_kcxqlayout_ktbj);

                        if (string.length > 2){
                            sksj.setText(week + changeTime(num*2-1) + changeTime(num*2));
                            kcmc.setText(string[2]);
                            skdd.setText(string[3]);
                            skjs.setText(string[4]);
                            ktbj.setText(string[5]);
                            kkzc.setText(FileUtil.getString(string[6]));

                            sksj.setFocusable(false);
                            kcmc.setFocusable(false);
                            skdd.setFocusable(false);
                            skjs.setFocusable(false);
                            ktbj.setFocusable(false);
                            kkzc.setFocusable(false);

                            myDialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("课程详情")
                                    .setPositiveButton("编辑", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view1) {
                                            String[] s = all.getText().toString().split("#");
                                            final View v = getLayoutInflater().inflate(R.layout.kcxq_layout, null);

                                            final EditText kcmc1 = (EditText)v.findViewById(R.id.id_kcxqlayout_kcmc);
                                            final EditText skdd1 = (EditText)v.findViewById(R.id.id_kcxqlayout_skdd);
                                            final EditText skjs1 = (EditText)v.findViewById(R.id.id_kcxqlayout_skjs);
                                            final EditText sksj1 = (EditText)v.findViewById(R.id.id_kcxqlayout_sksj);
                                            final EditText kkzc1 = (EditText)v.findViewById(R.id.id_kcxqlayout_kkzc);
                                            final EditText ktbj1 = (EditText)v.findViewById(R.id.id_kcxqlayout_ktbj);

                                            sksj1.setText(week + changeTime(num*2-1) + changeTime(num*2));
                                            kcmc1.setText(s[2]);
                                            skdd1.setText(s[3]);
                                            skjs1.setText(s[4]);
                                            ktbj1.setText(s[5]);
                                            kkzc1.setText(FileUtil.getString(s[6]));

                                            sksj1.setFocusable(false);
                                            dialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("编辑课程")
                                                    .setPositiveButton("修改", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            dialog.dismiss();
                                                        }

                                                    }).setView(v).create();
                                            myDialog.dismiss();
                                            dialog.show();
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                        }
                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                        }
                                    }).setView(view).create();
                            myDialog.show();
                        }else {
                            Toast.makeText(TimetableActivity.this,"长按添加课程",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final TextView all = v.findViewById(R.id.id_tablebox_all);
                        final String[] string = all.getText().toString().split("#");
                        if (string.length > 2){
                            AlertDialog.Builder builder=new AlertDialog.Builder(TimetableActivity.this);
                            builder.setTitle("系统消息");
                            builder.setMessage("确定删除" + "“"+ string[2] + "”？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    delete(week,num,string[2]);
                                }
                            });
                            builder.setNegativeButton("取消",null);
                            builder.show();
                        }else {
                            View view = getLayoutInflater().inflate(R.layout.kcxq_layout, null);
                            final EditText kcmc = (EditText)view.findViewById(R.id.id_kcxqlayout_kcmc);
                            final EditText skdd = (EditText)view.findViewById(R.id.id_kcxqlayout_skdd);
                            final EditText skjs = (EditText)view.findViewById(R.id.id_kcxqlayout_skjs);
                            final EditText sksj = (EditText)view.findViewById(R.id.id_kcxqlayout_sksj);
                            final EditText kkzc = (EditText)view.findViewById(R.id.id_kcxqlayout_kkzc);
                            final EditText ktbj = (EditText)view.findViewById(R.id.id_kcxqlayout_ktbj);
                            sksj.setText(week + changeTime(num*2-1) + changeTime(num*2));
                            sksj.setFocusable(false);
                            myDialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("添加课程")
                                    .setPositiveButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            insert(week,num,kcmc.getText().toString(),skdd.getText().toString()
                                                    ,skjs.getText().toString(), ktbj.getText().toString()
                                                    ,kkzc.getText().toString());
                                            myDialog.dismiss();
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                        }
                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                        }
                                    }).setView(view).create();
                            myDialog.show();
                        }
                        return false;
                    }
                });

                    linearLayout.addView(view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




//
//        for(int i = 1; i <= 6; i++){
//            for (int j = 1; j <= 7; j++) {
//                String a = "id_timetable_week" + j;
//                Resources res=getResources();
//                int x =res.getIdentifier(a,"id",getPackageName());
//                LinearLayout linearLayout = findViewById(x);
//                View view = LinearLayout.inflate(getContext(),R.layout.tablebox,null);
//                TextView all = view.findViewById(R.id.id_tablebox_all);
//                ShapeCornerBgView t = view.findViewById(R.id.id_tablebox_text);
//
//                String string = stringArray[i-1][j-1];
//                String[] string1 = string.split("\\*");
//                String showString = "";
//
//                for (int n = 0,len = string1.length; n < len ;n++){
//                    if (!string1[n].equals("&&&&")&&!string1[n].equals("")){
//                        String kcm = string1[n].split("#")[2].replace("&nbsp;","").replaceAll("\\s*", "");
//                        String kkzc = "#" + string1[n].split("#")[6];
//                        if(kkzc.contains("#" + theWeek + ",")||kkzc.contains("," + theWeek + ",") || kkzc.contains("," + theWeek + "周")||kkzc.contains("#" + theWeek + "周") ){
//                            showString = kcm + string1[n].split("#")[3];
//                            if (Arrays.asList(projectname).contains(kcm)){
//                                for(int k = 0;k<projectname.length;k++){
//                                    if(kcm.equals(projectname[k])){
//                                        t.setBgColor(getResources().getColor(colorArray[k]));
//                                    }
//                                }
//                            }else {
//                                t.setBgColor(getResources().getColor(colorArray[projectnum+1]));
//                                projectname[projectnum+1] = kcm;
//                                projectnum = projectnum+1;
//                            }
//                            all.setText(string1[n]);
//                            t.setTextColor(0xffffffff);
//                            break;
//                        }else {
//                            if (isShowNotCurrentWeek){
//                                all.setText(string1[n]);
//                                showString = "[非本周]" + string1[n].split("#")[2] + string1[n].split("#")[3];
//                                t.setBgColor(getResources().getColor(colorArray[0]));
//                            }else {
//                                all.setText(i + "#" + j);
//                            }
//                        }
//                    }else {
//                        all.setText(i + "#" + j);
//                    }
//                }
//                t.setText(showString.replace(" ",""));
//                t.setMaxLines(8);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final TextView all = v.findViewById(R.id.id_tablebox_all);
//                        String[] string = all.getText().toString().split("#");
//                        final View view = getLayoutInflater().inflate(R.layout.kcxq_layout, null);
//
//                        EditText kcmc = (EditText)view.findViewById(R.id.id_kcxqlayout_kcmc);
//                        EditText skdd = (EditText)view.findViewById(R.id.id_kcxqlayout_skdd);
//                        EditText skjs = (EditText)view.findViewById(R.id.id_kcxqlayout_skjs);
//                        EditText sksj = (EditText)view.findViewById(R.id.id_kcxqlayout_sksj);
//                        EditText kkzc = (EditText)view.findViewById(R.id.id_kcxqlayout_kkzc);
//                        EditText ktbj = (EditText)view.findViewById(R.id.id_kcxqlayout_ktbj);
//
//                        if (string.length > 2){
//                            sksj.setText(string[1] + changeTime(Integer.parseInt(string[0])*2-1) + changeTime(Integer.parseInt(string[0])*2));
//                            kcmc.setText(string[2].replace("&nbsp;","").replaceAll("\\s*", ""));
//                            skdd.setText(string[3].replace("&nbsp;","").replaceAll("\\s*", ""));
//                            skjs.setText(string[4].replace("&nbsp;","").replaceAll("\\s*", ""));
//                            ktbj.setText(string[5].replace("&nbsp;","").replaceAll("\\s*", ""));
//
//                            String[] kkzcString =  string[6].replace("周","").split(",");
//                            if (kkzcString.length == 1){
//                                kkzc.setText(string[6]);
//                            }else{
//                                if (isDiZhen(kkzcString)){
//                                    kkzc.setText(kkzcString[0] + "-" + kkzcString[kkzcString.length-1] + "周");
//                                }else {
//                                    kkzc.setText(string[6]);
//                                }
//                            }
//
//                            sksj.setFocusable(false);
//                            kcmc.setFocusable(false);
//                            skdd.setFocusable(false);
//                            skjs.setFocusable(false);
//                            ktbj.setFocusable(false);
//                            kkzc.setFocusable(false);
//
//                            myDialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("课程详情")
//                                    .setPositiveButton("编辑", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view1) {
//                                            String[] s = all.getText().toString().split("#");
//                                            final View v = getLayoutInflater().inflate(R.layout.kcxq_layout, null);
//
//                                            final EditText kcmc1 = (EditText)v.findViewById(R.id.id_kcxqlayout_kcmc);
//                                            final EditText skdd1 = (EditText)v.findViewById(R.id.id_kcxqlayout_skdd);
//                                            final EditText skjs1 = (EditText)v.findViewById(R.id.id_kcxqlayout_skjs);
//                                            final EditText sksj1 = (EditText)v.findViewById(R.id.id_kcxqlayout_sksj);
//                                            final EditText kkzc1 = (EditText)v.findViewById(R.id.id_kcxqlayout_kkzc);
//                                            final EditText ktbj1 = (EditText)v.findViewById(R.id.id_kcxqlayout_ktbj);
//
//                                            sksj1.setText(s[1] + changeTime(Integer.parseInt(s[0])*2-1) + changeTime(Integer.parseInt(s[0])*2));
//                                            kcmc1.setText(s[2].replace("&nbsp;","").replaceAll("\\s*", ""));
//                                            skdd1.setText(s[3].replace("&nbsp;","").replaceAll("\\s*", ""));
//                                            skjs1.setText(s[4].replace("&nbsp;","").replaceAll("\\s*", ""));
//                                            ktbj1.setText(s[5].replace("&nbsp;","").replaceAll("\\s*", ""));
//
//                                            String[] kkzcString =  s[6].replace("周","").split(",");
//                                            if (kkzcString.length == 1){
//                                                kkzc1.setText(s[6]);
//                                            }else{
//                                                if (isDiZhen(kkzcString)){
//                                                    kkzc1.setText(kkzcString[0] + "-" + kkzcString[kkzcString.length-1] + "周");
//                                                }else {
//                                                    kkzc1.setText(s[6]);
//                                                }
//                                            }
//
//                                            sksj1.setFocusable(false);
//                                            dialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("编辑课程")
//                                                    .setPositiveButton("修改", new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            String msg = checkComplie(kcmc1.getText().toString(),
//                                                                    skdd1.getText().toString(),kkzc1.getText().toString().replace("周",""));
//                                                            if (msg.equals("ok")){
//                                                                if (checkSameWeek(sksj1.getText().toString(),kkzc1.getText().toString(),all.getText().toString(),true)){
//                                                                    Toast.makeText(TimetableActivity.this,"开课周次有冲突",Toast.LENGTH_SHORT).show();
//                                                                }else {
//                                                                    addNew(kcmc1.getText().toString(),skdd1.getText().toString(),
//                                                                            skjs1.getText().toString(),sksj1.getText().toString(),
//                                                                            ktbj1.getText().toString(),kkzc1.getText().toString(),
//                                                                            all.getText().toString(),true);
//                                                                    show(readFile("timetable"+ stuUsername));
//                                                                    dialog.dismiss();
//                                                                }
//                                                            }else {
//                                                                Toast.makeText(TimetableActivity.this,msg,Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    }).setView(v).create();
//                                            myDialog.dismiss();
//                                            dialog.show();
//                                        }
//                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                        @Override
//                                        public void onCancel(DialogInterface dialogInterface) {
//                                        }
//                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @Override
//                                        public void onDismiss(DialogInterface dialogInterface) {
//                                        }
//                                    }).setView(view).create();
//                            myDialog.show();
//                        }else {
//                            Toast.makeText(TimetableActivity.this,"长按添加课程",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                view.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        final TextView all = v.findViewById(R.id.id_tablebox_all);
//                        final String[] string = all.getText().toString().split("#");
//                        if (string.length > 2){
//                            AlertDialog.Builder builder=new AlertDialog.Builder(TimetableActivity.this);
//                            builder.setTitle("系统消息");
//                            builder.setMessage("确定删除" + "“"+ string[2].replace("&nbsp;","").replaceAll("\\s*", "") + "”？");
//                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    delete(all.getText().toString(),Integer.parseInt(string[0]),Integer.parseInt(string[1]));
//                                    show(readFile("timetable"+ stuUsername));
//                                }
//                            });
//                            builder.setNegativeButton("取消",null);
//                            builder.show();
//
//                        }else {
//                            View view = getLayoutInflater().inflate(R.layout.kcxq_layout, null);
//                            final EditText kcmc = (EditText)view.findViewById(R.id.id_kcxqlayout_kcmc);
//                            final EditText skdd = (EditText)view.findViewById(R.id.id_kcxqlayout_skdd);
//                            final EditText skjs = (EditText)view.findViewById(R.id.id_kcxqlayout_skjs);
//                            final EditText sksj = (EditText)view.findViewById(R.id.id_kcxqlayout_sksj);
//                            final EditText kkzc = (EditText)view.findViewById(R.id.id_kcxqlayout_kkzc);
//                            final EditText ktbj = (EditText)view.findViewById(R.id.id_kcxqlayout_ktbj);
//
//                            sksj.setText(string[1] + changeTime(Integer.parseInt(string[0])*2-1) + changeTime(Integer.parseInt(string[0])*2));
//                            sksj.setFocusable(false);
//                            myDialog = new BaseDialog.Builder(TimetableActivity.this).setTitle("添加课程")
//                                    .setPositiveButton("确定", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            String msg = checkComplie(kcmc.getText().toString(),
//                                                    skdd.getText().toString(),kkzc.getText().toString().replace("周",""));
//                                            if (msg.equals("ok")){
//                                                if (checkSameWeek(sksj.getText().toString(),kkzc.getText().toString(),"",false)){
//                                                    Toast.makeText(TimetableActivity.this,"开课周次有冲突",Toast.LENGTH_SHORT).show();
//                                                }else {
//                                                    addNew(kcmc.getText().toString(),skdd.getText().toString(),
//                                                            skjs.getText().toString(),sksj.getText().toString(),
//                                                            ktbj.getText().toString(),kkzc.getText().toString(),
//                                                            "",false);
//                                                    show(readFile("timetable"+ stuUsername));
//                                                    myDialog.dismiss();
//                                                }
//                                            }else {
//                                                Toast.makeText(TimetableActivity.this,msg,Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                        @Override
//                                        public void onCancel(DialogInterface dialogInterface) {
//                                        }
//                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @Override
//                                        public void onDismiss(DialogInterface dialogInterface) {
//                                        }
//                                    }).setView(view).create();
//                            myDialog.show();
//                        }
//                        return false;
//                    }
//                });
//                linearLayout.addView(view);
//            }
//        }
    }

//    //检查一个字符串数组中是否存在某字符串
//    public static boolean isIn(String substring, String[] source) {
//        if (source == null || source.length == 0) {
//            return false;
//        }
//        for (int i = 0; i < source.length; i++) {
//            String aSource = source[i];
//            if (aSource.equals(substring)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
    //给个位数字前添0
    private String changeTime(int s) {
        String time = "" + s;
        if (s < 10){
            time = "0" + s;
        }
        return time;
    }

//    //格式化开课周次
//    private static String getString(String s){
//        String result = "";
//        if(s.contains(","))
//            result = s;
//        else if(s.contains("-")){
//            int minW = Integer.parseInt(s.split("-")[0]);
//            int maxW = Integer.parseInt(s.split("-")[1]);
//            for(int i = minW;i < maxW;i++)
//                result += i + ",";
//            result += maxW;
//        }
//        else
//            result = s;
//        return result;
//    }
//
//    //检验字符串数组内的数字是否递增
//    private Boolean isDiZhen(String[] ins){
//        for(int i = 0 ;i<ins.length;i++) {
//            while (Integer.parseInt(ins[i + 1]) - Integer.parseInt(ins[i]) == 1) {
//                return true;
//            }
//            return false;
//        }
//        return false;
//    }
//
//    //检验课程名称、上课地点、开课周次是否为空，且开课周次的格式是否符合标准
//    private String checkComplie(String kcmc,String skdd,String kkzc){
//        if (kcmc.replace(" ","").isEmpty()){
//            return "课程名称不能为空";
//        }else if (skdd.replace(" ","").isEmpty()) {
//            return"上课地点不能为空";
//        }else{
//            if (kkzc.replace(" ","").isEmpty()) {
//                return"开课周次不能为空";
//            }else {
//                String regex = "(\\d{1,2})(-)(\\d{1,2})|(\\d{1,2})|((,)*(\\d{1,2}(,)(\\d{1,2})))*";
//                Pattern pattern = Pattern.compile(regex);
//                Matcher matcher = pattern.matcher(kkzc);
//                if (matcher.matches()){
//                    return "ok";
//                }else {
//                    return "开课周次格式错误";
//                }
//            }
//        }
//    }

    public  void update(int week,int ctime,int value,String kcm,String skdd,String tname,String jxb,String time){
        String json = getFileFromSD(filePath+fileName);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String weekd = jsonObject.getString(week+"");
            JSONObject object = new JSONObject(weekd);
            String ct = object.getString(ctime+"");
            JSONArray jsonArray = new JSONArray(ct);
            int m = 0;
            boolean flag2 = false,flag1 = true;
            int k = 0;
            boolean flag = false;
            for(int i = 0,size = jsonArray.length();i<size;i++){
                if(jsonArray.getJSONObject(i).getString("value").equals(value+"")){
                    k = i;
                    flag = true;
                    break;
                }
            }
            if(flag) {
                JSONObject js = jsonArray.getJSONObject(k);
                for (int i = 0, size = jsonArray.length(); i < size; i++) {
                    if (i == k)
                        continue;
                    if (iscontains(jsonArray.getJSONObject(i).getString("time"), FileUtil.getString(time)+"周1")) {
                        flag1 = false;
                        break;
                    } else {
                        if (compile(jsonArray.getJSONObject(i).getString("time"), FileUtil.getString(time)+"周")) {
                            m = i;
                            flag2 = true;
                            break;
                        }
                    }
                }
                if(flag1){
                    if (flag2) {
                        js.put("value", m );
                        for (int n = m, size = jsonArray.length(); n < size; n++) {
                            JSONObject j2 = jsonArray.getJSONObject(n);
                            j2.put("value", n+1);
                            jsonArray.put(n, j2);
                        }
                    } else {
                        js.put("value", jsonArray.length());
                        for(int n = k+1, size = jsonArray.length(); n < size; n++){
                            JSONObject j2 = jsonArray.getJSONObject(n);
                            j2.put("value", n);
                            jsonArray.put(n, j2);
                        }
                    }
                    js.put("kcm", kcm);
                    js.put("skdd", skdd);
                    js.put("tname", tname);
                    js.put("jxb", jxb);
                    js.put("time", FileUtil.getString(time)+"周");
                    jsonArray.put(k, js);
                    object.put(ctime + "", jsonArray);
                    jsonObject.put(week + "", object);
                    Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"上课时间冲突，不能修改！",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(),"修改失败！",Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeTxtToFile(String.valueOf(jsonObject),filePath,fileName);
    }

    public  void insert(int week,int ctime,String kcm,String skdd,String tname,String jxb,String time){
        String json = getFileFromSD(filePath+fileName);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String weekd = jsonObject.getString(week+"");
            JSONObject object = new JSONObject(weekd);
            String ct = object.getString(ctime+"");
            JSONArray jsonArray = new JSONArray(ct);
            String j = "";
            j = "{\"value\":\"0\","+"\"kcm\":"+"\""+kcm+"\","+"\"skdd\":"+"\""+skdd+"\","+"\"tname\":"+"\""+tname+"\","+"\"jxb\":"+"\""+jxb+"\","+"\"time\":"+"\""+FileUtil.getString(time)+"周\""+"}";
            JSONObject j1 = new JSONObject(j);
            int k = 0;
            boolean flag1 = false,flag = true;
            for(int i=0,size = jsonArray.length();i<size;i++){
                if(iscontains(jsonArray.getJSONObject(i).getString("time"),j1.getString("time"))){
                    Toast.makeText(getApplicationContext(),"上课周次冲突，不能添加！！",Toast.LENGTH_SHORT).show();
                    flag = false;
                    break;
                }else{
                    if(compile(jsonArray.getJSONObject(i).getString("time"),j1.getString("time"))){
                        k = i;
                        flag1 = true;
                        break;
                    }
                }
            }
            if(flag){
                if(flag1){
                    j1.put("value",k+1);
                    for(int m = k,size = jsonArray.length();m<size;m++){
                        JSONObject j2 = jsonArray.getJSONObject(m);
                        j2.put("value",m+2);
                        jsonArray.put(m,j2);
                    }
                }
                else
                    j1.put("value",jsonArray.length()+1);
                jsonArray.put(j1);
                Toast.makeText(getApplicationContext(),"添加成功!!",Toast.LENGTH_SHORT).show();
            }
            object.put(ctime+"",jsonArray);
            jsonObject.put(week+"",object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeTxtToFile(String.valueOf(jsonObject),filePath,fileName);
    }



    public void delete(int week, int ctime, String kcm){
        String json = getFileFromSD(filePath+fileName);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String weekd = jsonObject.getString(week+"");
            JSONObject object = new JSONObject(weekd);
            String ct = object.getString(ctime+"");
            JSONArray jsonArray = new JSONArray(ct);
            int k=0;
            boolean flag = false;
            for(int i = 0,size = jsonArray.length();i<size;i++) {
                JSONObject j0 = jsonArray.getJSONObject(i);
                if(j0.length() == 0){
                    flag = false;
                    break;
                }
                else{
                    if (j0.getString("kcm").equals(kcm)) {
                        k = i;
                        flag = true;
                        break;
                    }
                }
            }
            if(flag ){
                jsonArray.remove(k);
                object.put(ctime+"",jsonArray);
                jsonObject.put(week+"",object);
                writeTxtToFile(String.valueOf(jsonObject),filePath,fileName);
                show();
                Toast.makeText(getApplicationContext(),"删除成功！！",Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getApplicationContext(),"不能删除！！",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean compile(String t1,String t2){
        boolean flag = true;
        String tq = t1.split("周")[0];
        String tp = t2.split("周")[0];
        if(tq.length() < 3)
            tq = tq;
        else
            tq = tq.split(",")[0];
        if(tp.length() < 3)
            tp = tp;
        else
            tp = tp.split(",")[0];
        if(Integer.parseInt(tq)<Integer.parseInt(tp))
            flag = false;
        return  flag;
    }
    public boolean iscontains(String t1,String t2){
        boolean flag = false;
        String tq;
        String tp;
        if(t1.length()<t2.length()){
            tq= t1.split("周")[0];
            tp= t2.split("周")[0];
        }else{
            tq = t2.split("周")[0];
            tp = t1.split("周")[0];
        }
        if(tq.length() < 3){
            String[] t3 = tp.split(",");
            for(int i = 0,size = t3.length;i<size;i++){
                if(t3[i].equals(tq)){
                    flag = true;
                    break;
                }
            }
        }
        else{
            String[] t = tq.split(",");
            String[] t3 = tp.split(",");
            for(int i = 0,size = t.length;i<size;i++){
                for(int j = 0,size1 = t3.length;j<size1;j++){
                    if(t[i].equals(t3[j])){
                        flag = true;
                        break;
                    }
                }
                if(flag)
                    break;
            }
        }
        return  flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
           initView();
    }
}