package com.qinhu.oneschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.DB.Student;
import com.qinhu.oneschool.MyClass.AndroidBug5497Workaround;
import com.qinhu.oneschool.MyClass.GetToken;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends Activity implements View.OnClickListener{
    private EditText registerusername;
    private EditText registerpassword;
    private EditText registerrepassword;
    private TextView registererr;
    private ImageView clear;
    private ImageView reclear;
    private TextView codebnt;
    private LinearLayout registerbutton;
    private EditText code;
    private LinearLayout rebackbtn;
    private String token = "";
    private String username;
    private String password;
    private String repassword;
    private String smsCode;

    private String stuUsername;
    private String stuPassword;

    private Handler handler = new Handler();
    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AndroidBug5497Workaround.assistActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(RegisterActivity.this,R.color.noColor); }

        initView();
        initEvent();
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
    }

    private void initView() {
        code = (EditText) findViewById(R.id.id_register_code);
        registererr = findViewById(R.id.id_register_err);
        reclear = (ImageView) findViewById(R.id.id_register_reclear);
        clear = (ImageView) findViewById(R.id.id_register_clear);
        codebnt = (TextView) findViewById(R.id.id_register_codebnt);
        rebackbtn = (LinearLayout) findViewById(R.id.id_register_reback);
        registerbutton = (LinearLayout) findViewById(R.id.id_registerbutton);
        registerusername = (EditText) findViewById(R.id.id_register_username);
        registerpassword = (EditText) findViewById(R.id.id_register_password);
        registerrepassword = (EditText) findViewById(R.id.id_register_repassword);

        Intent intent = getIntent();
        stuUsername= intent.getStringExtra("stuUsername");
        stuPassword= intent.getStringExtra("stuPassword");

    }

    private void initEvent() {
        clear.setOnClickListener(this);
        rebackbtn.setOnClickListener(this);
        reclear.setOnClickListener(this);
        codebnt.setOnClickListener(this);
        registerbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.id_registerbutton:
                register();
                break;
            case R.id.id_register_reback:
                finish();
                break;
            case R.id.id_register_codebnt:
                sendcode();
                break;
            case R.id.id_register_clear:
                if (registerpassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    registerpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    clear.setImageResource(R.drawable.eye_open);
                }else {
                    registerpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    clear.setImageResource(R.drawable.eye_close);
                }
                break;
            case R.id.id_register_reclear:
                if (registerrepassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    registerrepassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    reclear.setImageResource(R.drawable.eye_open);
                }else {
                    registerrepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    reclear.setImageResource(R.drawable.eye_close);
                }
                break;
            default:
                break;
        }
    }

    private void register() {
        username = registerusername.getText().toString();
        password = registerpassword.getText().toString();
        repassword = registerrepassword.getText().toString();
        smsCode = code.getText().toString();
        if(username.isEmpty()){
            registererr.setText("*用户名不能为空!");
        }else if(smsCode.isEmpty()){
            registererr.setText("*验证码不能为空!");
        }else if(password.isEmpty()){
            registererr.setText("*密码不能为空!");
        }else if(repassword.isEmpty()){
            registererr.setText("*请确认密码!");
        }else if(password.equals(repassword)){
            BmobSMS.verifySmsCode(username,smsCode, new UpdateListener() {
                @Override
                public void done(BmobException ex) {
                    if(ex==null){//短信验证码已验证成功
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String r= GetToken.GetRongCloudToken(username);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        final MyUser myUser = new MyUser();
                                        Student student = new Student();
                                        student.setXuehao(stuUsername);
                                        student.setPassword(stuPassword);
                                        myUser.setUsername(username);
                                        myUser.setPassword(password);
                                        myUser.setNickname("校校");
                                        myUser.setAvatar("http://www.oneschool.com.cn/default.png");
                                        myUser.setStu(student);
                                        myUser.setToken(r);
                                        myUser.signUp(new SaveListener<MyUser>() {
                                            @Override
                                            public void done(MyUser s, BmobException e) {
                                                if(e==null){
                                                    MyUser user =new MyUser();
                                                    user.setMobilePhoneNumber(s.getUsername());
                                                    user.setMobilePhoneNumberVerified(true);
                                                    user.update(s.getObjectId(),new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                Toast.makeText(RegisterActivity.this,"恭喜，注册成功！", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    registererr.setText(e.getErrorCode()+"");
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).start();

                    }else{
                        registererr.setText( "验证失败：code ="+ex.getErrorCode()+ex.getLocalizedMessage());
                    }
                }
            });
        }else {
            registererr.setText("*两次密码输入不一致！");
        }
    }

    private void sendcode() {
        final String username = registerusername.getText().toString();
        if(username.isEmpty()){
            registererr.setText("*请输入手机号！");
        }else {
            BmobQuery<MyUser> query = new BmobQuery<MyUser>();
            query.addWhereEqualTo("username", username);
            query.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, final BmobException e) {
                    if(e == null){
                        if (list.size() == 0){
                            BmobSMS.requestSMSCode(username, "OneSchool",new QueryListener<Integer>() {
                                @Override
                                public void done(Integer smsId, BmobException ex) {
                                    if(ex==null){//验证码发送成功
                                        time.start();//开始计时
                                        Toast.makeText(RegisterActivity.this,"短信发送成功！", Toast.LENGTH_SHORT).show();
                                    }else {

                                    }
                                }
                            });
                        }else {
                            registererr.setText("用户已存在！");
                        }
                    }
                }
            });


        }
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            codebnt.setClickable(false);
            codebnt.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            codebnt.setText("重新验证");
            codebnt.setClickable(true);
        }
    }
}
