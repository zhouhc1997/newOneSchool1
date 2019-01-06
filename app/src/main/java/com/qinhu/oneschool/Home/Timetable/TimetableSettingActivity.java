package com.qinhu.oneschool.Home.Timetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.qinhu.oneschool.DB.AutoBackground;
import com.qinhu.oneschool.MyClass.BaseDialog;
import com.qinhu.oneschool.MyClass.JiaowuLogin;
import com.qinhu.oneschool.MyClass.PickerView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.FileUtil;
import com.qinhu.oneschool.Utils.FormatTime;
import com.qinhu.oneschool.Utils.StatusBarUtil;
import com.qinhu.oneschool.Utils.Tools;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

import static com.qinhu.oneschool.MyApplication.getContext;
import static com.qinhu.oneschool.Utils.FileUtil.writeTxtToFile;

public class TimetableSettingActivity extends AppCompatActivity {
    private ImageView backbtn;
    private ProgressDialog progressDialog;
    private static final int PHOTO_REQUEST_GALLERY = 1;// 从相册中选择
    private  String filePath = Environment.getExternalStorageDirectory().getPath() + "/oneschool/";
    private String fileName = "";

    private LinearLayout dqxqbox;
    private LinearLayout dqzcbox;
    private LinearLayout updatebox;
    private LinearLayout backgroundbox;

    private ImageView background;
    private TextView dqxq;
    private TextView dqzc;
    private TextView updatetime;

    private BaseDialog dialog;
    private SwitchCompat aSwitch;
    private SwitchCompat bSwitch;

    private Handler handler;

    private String lastJSESSIONID;
    private String theCurrentWeek = "1";

    private String stuUsername;
    private String stuPassword;

    private HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_setting);

        StatusBarUtil.noColorBar(TimetableSettingActivity.this);

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        stuUsername = preferences.getString("stuUsername",null);
        stuPassword = preferences.getString("stuPassword",null);
        fileName = stuUsername + ".json";

        initView();
        initEvent();
    }

    private void initView() {
        handler = new Handler();
        backbtn = findViewById(R.id.id_mheader_backbtn);
        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("课表设置");

        aSwitch = (SwitchCompat) findViewById(R.id.id_timetablesetting_showselect);
        bSwitch = (SwitchCompat) findViewById(R.id.id_timetablesetting_todayselect);
        dqxqbox = findViewById(R.id.id_timetablesetting_dqxqbox);
        dqzcbox =findViewById(R.id.id_timetablesetting_dqzcbox);
        updatebox = findViewById(R.id.id_timetablesetting_updatebox);
        backgroundbox = findViewById(R.id.id_timetablesetting_backgroundbox);

        horizontalScrollView = findViewById(R.id.id_timetablesetting_horizontalscrollview);

        dqxq = findViewById(R.id.id_timetablesetting_dqxq);
        dqzc =findViewById(R.id.id_timetablesetting_dqzc);
        updatetime  = findViewById(R.id.id_timetablesetting_updatetime);
        background = findViewById(R.id.id_timetablesetting_background);

        SharedPreferences preferences = getSharedPreferences("timetable" + stuUsername,MODE_PRIVATE);
        if (!preferences.contains("dqxq")){
            dqxq.setText("2018-2019-1");
        }else {
            dqxq.setText(preferences.getString("dqxq",null));
        }

        if (preferences.contains("background")){
            Glide.with(TimetableSettingActivity.this).load(preferences.getString("background",null)).into(background);
        }

        if (!preferences.contains("dqzc")||!preferences.contains("dqzcmonday")){
            dqzc.setText("1");
        }else {
            int days = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(preferences.getString("dqzcmonday",null));
                days = FormatTime.differentDaysByMillisecond(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dqzc.setText(Integer.parseInt(preferences.getString("dqzc",null)) + (days+1)/7 + "");
        }

        if (!preferences.contains("lasttime")){
            updatetime.setText("");
        }else {
            updatetime.setText("上次更新:" + FormatTime.show(preferences.getString("lasttime",null)));
        }

        if (preferences.contains("isShowNotCurrentWeek")){
            aSwitch.setChecked(preferences.getBoolean("isShowNotCurrentWeek",true));
        }

        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        if (sharedPreferences.contains("isTodayLaunch")){
            bSwitch.setChecked(sharedPreferences.getBoolean("isTodayLaunch",true));
        }
    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updatebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerify();
            }
        });
        dqxqbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
            }
        });
        dqzcbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek();
            }
        });
        backgroundbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (horizontalScrollView.getVisibility()==View.GONE){
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    showImages();
                }else {
                    horizontalScrollView.setVisibility(View.GONE);
                }
            }
        });
        //设置是否显示非本周
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isShowNotCurrentWeek",isChecked);
                editor.commit();
                Intent intent=new Intent();
                setResult(1,intent);//发送返回码
            }
        });

        //设置是否显示课表启动页
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isTodayLaunch",isChecked);
                editor.commit();
            }
        });
    }

    private void showImages() {
        final LinearLayout linearLayout = findViewById(R.id.id_timetablesetting_imagesbox);
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.backgroundbox, null);
        ImageView imageView = v.findViewById(R.id.id_backgroundbox_img);
        imageView.setImageResource(R.drawable.camerabox);
        v.setLayoutParams(lp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();
            }
        });
        linearLayout.addView(v);

        BmobQuery<AutoBackground> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type","timetable");
        bmobQuery.setLimit(10);
        bmobQuery.findObjects(new FindListener<AutoBackground>() {
            @Override
            public void done(List<AutoBackground> list, BmobException e) {
                if (e==null){
                    int size = list.size();
                    for (int i = 0; i < size; i++){
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LayoutInflater inflater3 = LayoutInflater.from(getContext());
                        View v = inflater3.inflate(R.layout.backgroundbox, null);
                        ImageView imageView = v.findViewById(R.id.id_backgroundbox_img);
                        TextView textView = v.findViewById(R.id.id_backgroundbox_text);
                        Glide.with(getContext())
                                .load(list.get(i).getImgPath())
                                .into(imageView);
                        textView.setText(list.get(i).getImgPath());
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShowProgress();
                                TextView textView1 = v.findViewById(R.id.id_backgroundbox_text);
                                String imgPath = textView1.getText().toString();
                                String fileName = System.currentTimeMillis() + ".png";
                                BmobFile bmobfile =new BmobFile(fileName,"",imgPath);
                                downloadFile(bmobfile);
                            }
                        });
                        v.setLayoutParams(lp);
                        linearLayout.addView(v);
                    }
                }
            }
        });
    }

    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void done(String s,BmobException e) {
                if(e==null){
                    SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("background", s);
                    editor.commit();
                    CloseProgress();
                    Glide.with(TimetableSettingActivity.this).load(s).into(background);
                    Toast.makeText(TimetableSettingActivity.this,s,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }
        });
    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                initUCrop(uri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            Glide.with(TimetableSettingActivity.this).load(UCrop.getOutput(data)).into(background);
            if (data != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("background", String.valueOf(UCrop.getOutput(data)));
                editor.commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initUCrop(Uri uri) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));
        UCrop.Options options = new UCrop.Options();
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setCircleDimmedLayer(false);

        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 2)
                .withMaxResultSize(10800, 6400)
                .withOptions(options)
                .start(this);
    }

    //设置当前周
    private void selectWeek() {
        View view = getLayoutInflater().inflate(R.layout.weekpicker, null);
        PickerView week = (PickerView) view.findViewById(R.id.id_pickerview_week);
        List<String> WeekData = new ArrayList<String>();
        for (int i = 1;i <= 25;i++){
            WeekData.add("" + i);
        }
        theCurrentWeek = dqzc.getText().toString();
        week.setData(WeekData,Integer.parseInt(theCurrentWeek)-1);
        week.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                theCurrentWeek = text;
            }
        });
        dialog = new BaseDialog.Builder(this).setTitle("请选择当前周次")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("dqzc",theCurrentWeek);
                        editor.putString("dqzcmonday",FormatTime.getMondayOFWeek());
                        editor.commit();
                        dqzc.setText(theCurrentWeek);
                        Intent intent=new Intent();
                        setResult(1,intent);//发送返回码
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

    //设置当前学期
    private void selectType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TimetableSettingActivity.this);
        builder.setTitle("请选择学期");

        List<String> data_list = new ArrayList<>();
        int startYear = Integer.parseInt(stuUsername.substring(0,4));
        int currentYear = FormatTime.getCurrentYear();
        for (;startYear<=currentYear;startYear++){
            String str = startYear + "-" + (startYear+1) + "-";
            if (startYear!=currentYear){
                data_list.add(str + 1);
            }else{
                if (FormatTime.getCurrentMonth()>8){
                    data_list.add(str + 1);
                }
            }
            if (startYear!=currentYear){
                data_list.add(str +2);
            }
        }
        final String[] styles = new String[data_list.size()];
        for (int i = 0;i<data_list.size();i++){
           styles[i] = data_list.get(i);
        }
        builder.setItems(styles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dqxq.setText(styles[which]);
            }
        });
        builder.show();
    }

    //获取验证码
    private void getVerify(){
        final View view = getLayoutInflater().inflate(R.layout.edit_code, null);
        final EditText amountEdit = (EditText) view.findViewById(R.id.id_edit_code_edit);
        final ImageView codeimg = (ImageView) view.findViewById(R.id.id_edit_code_img);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl= JiaowuLogin.WustIpString() +"verifycode.servlet";
                final Bitmap r= crawler(httpUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        codeimg.setImageBitmap(r);
                        dialog = new BaseDialog.Builder(TimetableSettingActivity.this).setTitle("请输入验证码")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final String vcode = amountEdit.getText().toString();
                                        if (vcode.isEmpty()){
                                            Toast.makeText(TimetableSettingActivity.this,"*验证码不能为空!",Toast.LENGTH_SHORT).show();
                                        }else {
                                            ShowProgress();
                                            upData(vcode);
                                        }
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
                });
            }
        }).start();

    }

    //更新课表
    private void upData(final String vcode) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String httpUrl= JiaowuLogin.WustIpString()+"Logon.do?method=logon";
                            final String i = JiaowuLogin.login(httpUrl,stuUsername,stuPassword,vcode,lastJSESSIONID);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (i == "1"){
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String httpUrl= JiaowuLogin.WustIpString() + "tkglAction.do?";
                                                String d = "method=goListKbByXs&istsxx=no&xnxqh="+ dqxq.getText().toString() +"&zc=&xs0101id=" + stuUsername;
                                                final String r= JiaowuLogin.crawler(httpUrl,d,lastJSESSIONID);
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d("tag",r);
                                                        saveFile(r);
                                                        Toast.makeText(TimetableSettingActivity.this,"课表已更新！",Toast.LENGTH_SHORT).show();
                                                        SharedPreferences sharedPreferences = getSharedPreferences("timetable" + stuUsername, Context.MODE_PRIVATE); //私有数据
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("dqxq",dqxq.getText().toString());
                                                        Date dt = new Date();
                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                                                        editor.putString("lasttime",sdf.format(dt));
                                                        editor.commit();
                                                        Intent intent=new Intent();
                                                        setResult(1,intent);//发送返回码
                                                        updatetime.setText("上次更新:刚刚");
                                                        CloseProgress();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }else{
                                        Toast.makeText(TimetableSettingActivity.this,i,Toast.LENGTH_SHORT).show();
                                        CloseProgress();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }).start();
        }

    //将课程表保存到本地数据库中
    private void saveFile(String strHTML) {
        Document document = Jsoup.parse(strHTML);
        String[] str = new String[7];
        for(int i = 1; i<=7;i++) {
            String string = "\'"+i+ "\':{" ;
            String s1 = "";
            for (int j = 1; j <= 6; j++) {
                Elements elements = document.select("#" + j + "-" + i + "-" + "2");
                s1 += "\'"+j + "\':";
                if (elements.size() > 1) {
                    s1+="[";
                    for (int m = 0, size = elements.size(); m < size - 1; m++) {
                        if (elements.get(m).hasText())
                            s1 += getDemo(elements.get(m).toString())+",";
                    }
                    if(j != 6)
                        s1 += getDemo(elements.get(elements.size() - 1).toString())+"],";
                    else
                        s1 += getDemo(elements.get(elements.size() - 1).toString())+"]}";
                } else if (elements.size() == 1 && elements.hasText()){
                    if(!iscontain(elements.get(0).toString())){
                        s1+="[";
                        if(j != 6)
                            s1 += getDemo(elements.get(0).toString())+"{}],";
                        else
                            s1 += getDemo(elements.get(0).toString())+"{}]}";
                    }else{
                        if(j != 6)
                            s1 += getDemo(elements.get(0).toString())+",";
                        else
                            s1 += getDemo(elements.get(0).toString())+"}";
                    }
                } else {
                    s1+="[";
                    if(j != 6)
                        s1 += "{}],";
                    else
                        s1 += "{}]}";
                }
            }
            string += s1;
            str[i-1] = string;
        }
        String json = "{";
        for(int a = 0;a<6;a++){
            json += str[a]+",";
        }
        json += str[6]+"}";

        writeTxtToFile(json,filePath,fileName);
    }


    public boolean iscontain(String s){
        String s2 = s.split("&nbsp;")[1];
        boolean flag = true;
        if(s2.contains("<br>"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    //将数据转换成这种格式 >>  Ttime + "#" + Ctime + "#" + s1[0] + "#" + s1[3] + "#" + Tname + "#" + s1[1] + "#" + Time + "周";

    public String getDemo(String s) {
        String[] s1 = s.split("&nbsp;")[1].replace("<nobr>","").split("<br>");
        String result = "";
        if(s1.length == 5){
            String TT = s1[2].split("周\\[")[0];
            String Tname = TT.split(" ")[0];
            String Time =  FileUtil.getString(TT.split(" ")[1]);
            s1[0] = "\'"+s1[0]+"\'";
            s1[3] = "\'"+s1[3]+"\'";
            Tname = "\'"+Tname +"\'";
            s1[1] = "\'"+s1[1]+"\'";
            Time = "\'"+Time+"周\'";
            result = "[{\'value\':"+"\'1\',"+"\'kcm\'" + ":"+s1[0].replaceAll("\\s*", "")+ ","  +"\n" + "\'skdd\'"+ ":" + s1[3].replaceAll("\\s*", "") +","+"\n" + "\'tname\':" +Tname.replaceAll("\\s*", "") + ","+"\n"+"\'jxb\':" + s1[1].replaceAll("\\s*", "") + ","+"\n"+"\'time\':" + Time.replaceAll("\\s*", "")+"}]";
        }
        if(s1.length>5){
            result = "[";
            for(int i = 0,size = s1.length-5;i<size;i = i+4 ){
                String TT = s1[i+2].split("周\\[")[0];
                String tname = TT.split(" ")[0];
                String time =  FileUtil.getString(TT.split(" ")[1]);
                result += "{\'value\':\'"+String.valueOf(i/4+1)+"\',\'kcm\':\'"+s1[i].replaceAll("\\s*", "")+"\',\'skdd\':\'"+s1[i+3].replaceAll("\\s*", "")+"\',\'tname\':\'"+tname.replaceAll("\\s*", "")+"\',\'jxb\':\'"+s1[i+1].replaceAll("\\s*", "")+"\',\'time\':\'"+time.replaceAll("\\s*", "")+"周\'},";
            }
            result += "{\'value\':\'"+String.valueOf(s1.length/4)+"\',\'kcm\':\'"
                    +s1[s1.length-5].replaceAll("\\s*", "")
                    +"\',\'skdd\':\'"+s1[s1.length-2].replaceAll("\\s*", "")
                    +"\',\'tname\':\'"+s1[s1.length-3].split("周\\[")[0].split(" ")[0].replaceAll("\\s*", "")
                    +"\',\'jxb\':\'"+s1[s1.length-4].replaceAll("\\s*", "")
                    +"\',\'time\':\'"+ FileUtil.getString(s1[s1.length-3].split("周\\[")[0].split(" ")[1]).replaceAll("\\s*", "")+"周\'}]";
        }
        return result;
    }

    public Bitmap crawler(String httpUrl) {
        Bitmap msg = null; //服务器返回结果
        try {
            //创建URL对象
            URL url = new URL(httpUrl);
            //创建HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    url.openConnection();
            //设置连接相关属性
            httpURLConnection.setConnectTimeout(5000); //设置连接超时为5秒
            httpURLConnection.setRequestMethod("GET"); //设置请求方式(默认为get)
            httpURLConnection.setRequestProperty("Charset", "UTF-8"); //设置uft-8字符集
            httpURLConnection.setRequestProperty("Referer", "http://jwc.wust.edu.cn/");
            //建立到连接(可省略)
            httpURLConnection.connect();
            //获得服务器反馈状态信息
            //200：表示成功完成(HTTP_OK)， 404：请求资源不存在(HTTP_NOT_FOUND)
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream bufferedReader = httpURLConnection.getInputStream();
                msg = BitmapFactory.decodeStream(bufferedReader);
                Map<String,List<String>> map = httpURLConnection.getHeaderFields();
                lastJSESSIONID = map.get("Set-Cookie").get(0).split(";")[0] + ";" + map.get("Set-Cookie").get(1).split(";")[0];
                httpURLConnection.disconnect();
            }
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void ShowProgress(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(TimetableSettingActivity.this);
            progressDialog.setMessage("正在查询....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public void CloseProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
