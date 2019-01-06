package com.qinhu.oneschool.Home;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.baidubce.model.User;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qinhu.oneschool.DB.Grade;
import com.qinhu.oneschool.DB.Grades;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.MyClass.JiaowuLogin;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.FormatTime;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

public class GradesActivity extends Activity {

    private Handler handler;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private HomeAdapter gradesAdapter;
    private String xueqi = "2018-2019-1";
    private ArrayList<String[]> Gradeslist = new ArrayList<>();
    private List<Grades> grades = new ArrayList<>();
    private Spinner spinner;
    private ArrayList<String> data_list;
    private ArrayAdapter<String> arr_adapter;

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    private LinearLayout loginbox;
    private String lastJSESSIONID;
    private ImageView backbtn;
    private ImageView codeImg;
    private EditText code;
    private TextView err;
    private ShapeCornerBgView loginbtn;
    private String stuUsername;
    private String stuPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(GradesActivity.this,R.color.white); }

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        stuUsername = preferences.getString("stuUsername",null);
        stuPassword = preferences.getString("stuPassword",null);


            initView();
            initEvent();
    }

    private void initEvent() {

        codeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodeImg();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKbTwo();
                finish();
            }
        });
        loginbtn.setShadowLayer(10f,6f,6f,Color.GRAY);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().isEmpty()){
                    err.setText("*验证码不能为空!");
                }else {
                    ShowProgress();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String httpUrl= JiaowuLogin.WustIpString()+"Logon.do?method=logon";
                            final String i = JiaowuLogin.login(httpUrl,stuUsername,stuPassword,code.getText().toString(),lastJSESSIONID);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                if (i == "1"){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String httpUrl= JiaowuLogin.WustIpString() + "xszqcjglAction.do?method=queryxscj";
                                            String d = "xsfs=qbcj&kksj=" + xueqi;
                                            final String r= JiaowuLogin.crawler(httpUrl,d,lastJSESSIONID);
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    saveGrades(r);
                                                    loginbox.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }).start();
                                }else{
                                    err.setText(i);
                                    getCodeImg();
                                    CloseProgress();
                                }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        //数据
        data_list = new ArrayList<>();
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

        //适配器
        arr_adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                xueqi = data_list.get(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initView() {
        handler = new Handler();
        recyclerView = findViewById(R.id.gradeslist);
        codeImg = findViewById(R.id.id_grades_codeimg);
        backbtn = findViewById(R.id.id_mheader_backbtn);
        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("成绩查询");

        code = findViewById(R.id.id_grades_code);
        loginbox = findViewById(R.id.id_grades_loginbox);
        spinner = findViewById(R.id.spinner);
        err = findViewById(R.id.id_grades_err);
        loginbtn = findViewById(R.id.id_grades_loginbutton);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(GradesActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager1);
        getCodeImg();
    }

    private void getCodeImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl=
                        JiaowuLogin.WustIpString()+"verifycode.servlet";
                final Bitmap r= crawler(httpUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        codeImg.setImageBitmap(r);
                    }
                });
            }
        }).start();
    }

    private void saveGrades(String r){
        org.jsoup.nodes.Document doc = Jsoup.parse(r);
        Elements element = doc.getElementsByClass("smartTr");
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);
        final List<BmobObject> grades = new ArrayList<BmobObject>();
        for (int i = 0;i<element.size();i++){
            String flag = element.get(i).child(11).text();
            if (flag.equals("正常考试")){
                flag = "";
            }else {
                flag = "（" + flag + ")";
            }
            String[] a = new String[5];
            a[0] = flag + element.get(i).child(4).text();
            a[1] = element.get(i).child(5).text();
            a[2] = element.get(i).child(10).text();
            a[3] = element.get(i).child(13).text();
            a[4] = element.get(i).child(3).text();
            Gradeslist.add(a);
            Grades grades1 = new Grades();
            grades1.setGrade(element.get(i).child(5).text());
            grades1.setProjectname(element.get(i).child(4).text());
            grades1.setUser(user);
            grades.add(grades1);
        }

        BmobQuery<Grades> query = new BmobQuery<>();
        query.addWhereEqualTo("user",user);
        query.findObjects(new FindListener<Grades>() {
            @Override
            public void done(List<Grades> list, BmobException e) {
                if (e==null){
                    BmobBatch batch =new BmobBatch();
                    //批量删除
                    List<BmobObject> persons2 = new ArrayList<BmobObject>();
                    for (int i = 0,size = list.size();i < size;i++){
                        Grades p2 = new Grades();
                        p2.setObjectId(list.get(i).getObjectId());
                        persons2.add(p2);
                    }
                    batch.deleteBatch(persons2);
                    batch.insertBatch(grades);
                    batch.doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                        }
                    });
                }
            }
        });
        CloseProgress();
        show();
    }

    private void show() {
        for (int i = 0,size = Gradeslist.size();i<size;i++){
            Boolean jige = true;
            if(Character.isDigit(Gradeslist.get(i)[1].charAt(0))){
                if(Integer.parseInt(Gradeslist.get(i)[1])<60 ){
                    jige = false;
                }
            }else {
                if(Gradeslist.get(i)[1].equals("E" )){
                    jige = false;
                }
            }
            Grades grade = new Grades(Gradeslist.get(i)[0],Gradeslist.get(i)[1],Gradeslist.get(i)[2],Gradeslist.get(i)[3],jige);
            grades.add(grade);
        }
        gradesAdapter = new HomeAdapter(R.layout.gradeslist,grades);
        recyclerView.setAdapter(gradesAdapter);
        gradesAdapter.openLoadAnimation();
        gradesAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT );
    }

    public class HomeAdapter extends BaseQuickAdapter<Grades, BaseViewHolder> {
        public HomeAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(final BaseViewHolder helper, final Grades item) {
            helper.setText(R.id.projectname,item.getProjectname())
                    .setText(R.id.id_grade,item.getGrade())
                    .setText(R.id.id_xuefen,item.getXuefen())
                    .setTextColor(R.id.id_grade,item.getJige()?getResources().getColor(R.color.selected): Color.RED)
                    .setText(R.id.id_jidian,item.getJidian());
        }
        @Override
        protected void startAnim(Animator anim, int index) {
            super.startAnim(anim, index);
            anim.setStartDelay(index * 150);
        }
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
            progressDialog=new ProgressDialog(GradesActivity.this);
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

    //此方法只是关闭软键盘 可以在finish之前调用一下
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
