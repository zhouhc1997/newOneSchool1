package com.qinhu.oneschool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qinhu.oneschool.MyClass.AndroidBug5497Workaround;
import com.qinhu.oneschool.MyClass.JiaowuLogin;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class BeforeRegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private String lastJSESSIONID;
    private TextView err;

    private Handler handler;

    private EditText truename;
    private EditText stuUsername;
    private EditText stuPassword;
    private EditText code;

    private ImageView codeImg;
    private ImageView truenameclear;
    private ImageView usernameclear;
    private ImageView passwordclear;

    private LinearLayout button;
    private LinearLayout backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_register);
        AndroidBug5497Workaround.assistActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(BeforeRegisterActivity.this,R.color.noColor); }

            initView();
            initEvent();
    }


    private void initView() {
        handler = new Handler();
        err = findViewById(R.id.id_beforeregister_err);

        truename= findViewById(R.id.id_beforeregister_truename);
        stuUsername= findViewById(R.id.id_beforeregister_username);
        stuPassword= findViewById(R.id.id_beforeregister_password);
        code= findViewById(R.id.id_beforeregister_code);

        codeImg= findViewById(R.id.id_beforeregister_codeimg);
        truenameclear = findViewById(R.id.id_beforeregister_clear);
        usernameclear = findViewById(R.id.id_beforeregister_usernameclear);
        passwordclear = findViewById(R.id.id_beforeregister_passwordclear);

        button = findViewById(R.id.id_beforeregisterbutton);
        backbtn = findViewById(R.id.id_beforeregister_reback);

        getCodeImg();
    }

    private void getCodeImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl= JiaowuLogin.WustIpString() + "verifycode.servlet";
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

    private void initEvent() {
        codeImg.setOnClickListener(this);
        truenameclear.setOnClickListener(this);
        usernameclear.setOnClickListener(this);
        passwordclear.setOnClickListener(this);
        button.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_beforeregister_reback:
                finish();
                break;
            case R.id.id_beforeregister_codeimg:
                getCodeImg();
                break;
            case R.id.id_beforeregister_clear:
                truename.setText("");
                break;
            case R.id.id_beforeregister_usernameclear:
                stuUsername.setText("");
                break;
            case R.id.id_beforeregister_passwordclear:
                if (stuPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    stuPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordclear.setImageResource(R.drawable.eye_open);
                }else {
                    stuPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordclear.setImageResource(R.drawable.eye_close);
                }
                break;
            case R.id.id_beforeregisterbutton:
                if (truename.getText().toString().isEmpty()){
                    err.setText("*真实姓名不能为空!");
                }else if (stuUsername.getText().toString().isEmpty()){
                    err.setText("*学号不能为空!");
                }else if (stuPassword.getText().toString().isEmpty()){
                    err.setText("*密码不能为空!");
                }else if (code.getText().toString().isEmpty()){
                    err.setText("*验证码不能为空!");
                }else {
                    checkStudent();
                }
                break;
            default:
                break;
        }

    }

    private void checkStudent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl= JiaowuLogin.WustIpString() + "Logon.do?method=logon";
                final String b = JiaowuLogin.login(httpUrl,stuUsername.getText().toString(),stuPassword.getText().toString(),code.getText().toString(),lastJSESSIONID);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (b == "1"){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String httpUrl = JiaowuLogin.WustIpString()+ "framework/main.jsp";
                                    final String r= JiaowuLogin.getString(httpUrl,lastJSESSIONID);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            org.jsoup.nodes.Document doc = Jsoup.parse(r);
                                            String name = doc.title().toString().split("\\[")[0];
                                            if (name.equals(truename.getText().toString())){
                                                Intent intent = new Intent(BeforeRegisterActivity.this,RegisterActivity.class);
                                                intent.putExtra("stuUsername",stuUsername.getText().toString());
                                                intent.putExtra("stuPassword",stuPassword.getText().toString());
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.search_in,R.anim.no_move);
                                                finish();
                                            }else {
                                                err.setText("*学校验证失败！");
                                                getCodeImg();
                                            }
                                        }
                                    });
                                  }
                                }).start();
                        }else{
                            err.setText(b);
                            getCodeImg();
                        }
                    }
                });
            }
        }).start();

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


}
