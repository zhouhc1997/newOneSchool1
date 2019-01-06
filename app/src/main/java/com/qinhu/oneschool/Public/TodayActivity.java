package com.qinhu.oneschool.Public;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.qinhu.oneschool.CommonAction;
import com.qinhu.oneschool.MainActivity;
import com.qinhu.oneschool.MyClass.Logo;
import com.qinhu.oneschool.R;
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
import java.util.Date;

import io.rong.imkit.RongIM;

public class TodayActivity extends AppCompatActivity {

    private Logo tohome;
    private String stuUsername;
    private static String filePath = "/sdcard/oneschool/";
    private int theCurrentWeek;
    private  ImageView imgBackground;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackHelper.onCreate(this);
        setContentView(R.layout.activity_today);

        StatusBarUtil.noColorBar(TodayActivity.this);

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        stuUsername = preferences.getString("stuUsername",null);

        imgBackground = findViewById(R.id.id_today_background);
        if (preferences.contains("imagePath"+ FormatTime.Current_Date())){
            Glide.with(TodayActivity.this).load(preferences.getString("imagePath"+ FormatTime.Current_Date(),null)).into(imgBackground);
        }else {
            Glide.with(TodayActivity.this).load(R.drawable.winner).into(imgBackground);
        }

        tohome = findViewById(R.id.id_today_tohome);
        tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodayActivity.this,MainActivity.class));
                finish();
            }
        });

        SharedPreferences prefer = getSharedPreferences("timetable" + stuUsername,MODE_PRIVATE);
        if (!prefer.contains("dqzc")||!prefer.contains("dqzcmonday")){
            theCurrentWeek = 1;
        }else {
            int days = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(prefer.getString("dqzcmonday",null));
                days = FormatTime.differentDaysByMillisecond(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            theCurrentWeek = Integer.parseInt(prefer.getString("dqzc",null)) + (days+1)/7 ;
        }
        show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void show(){
        int i = FormatTime.getWeekOfDate(new Date());
        TextView CurrentDate = findViewById(R.id.id_today_date);
        TextView CurrentWeek= findViewById(R.id.id_today_week);
        CurrentDate.setText(FormatTime.CurrentDate());
        CurrentWeek.setText(FormatTime.getWeekStringOfDate(new Date()));

        LinearLayout linearLayout = findViewById(R.id.id_today_box);
        TextView noneProject = findViewById(R.id.id_today_none);

        try {
            JSONObject jsonObject = new JSONObject(getFileFromSD(filePath + stuUsername + ".json"));
            String day = jsonObject.getString(i+"");
            JSONObject object = new JSONObject(day);
            for(int j = 1;j <= 6;j++) {
                String info = object.getString(j + "");
                JSONArray in = new JSONArray(info);

                for(int m = 0,size = in.length();m<size;m++) {
                    JSONObject object1 = in.getJSONObject(m);
                    if (object1.length() == 0)
                        continue;
                    String kcm = object1.getString("kcm");
                    String skdd = object1.getString("skdd");
                    String time = object1.getString("time");

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LayoutInflater inflater1 = LayoutInflater.from(TodayActivity.this);
                    View view = inflater1.inflate(R.layout.todaylist, null);

                    if (iscontains(time,theCurrentWeek + "周")){
                        if (noneProject != null){
                            noneProject.setVisibility(View.GONE);
                        }
                        TextView textViewkcm = view.findViewById(R.id.id_todaylist_kcm);
                        textViewkcm.setText(kcm);
                        TextView textViewskdd = view.findViewById(R.id.id_todaylist_skdd);
                        textViewskdd.setText(skdd);
                        TextView textViewnum = view.findViewById(R.id.id_todaylist_number);
                        textViewnum.setText((j*2-1) + "~" + (j *2));

                        TextView textViewsksj = view.findViewById(R.id.id_todaylist_sksj);

                        switch (j){
                            case 1:
                                textViewsksj.setText("8:20-10:00");
                                break;
                            case 2:
                                textViewsksj.setText("10:15-11:55");
                                break;
                            case 3:
                                textViewsksj.setText("14:10-15:50");
                                break;
                            case 4:
                                textViewsksj.setText("16:00-17:40");
                                break;
                            case 5:
                                textViewsksj.setText("19:00-20:40");
                                break;
                            case 6:
                                textViewsksj.setText("20:50-22:30");
                                break;
                            default:break;
                        }

                        view.setLayoutParams(lp);
                        linearLayout.addView(view);
                        break;
                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            CommonAction.getInstance().OutSign();
            finish();
            System.exit(0);
            RongIM.getInstance().disconnect();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
