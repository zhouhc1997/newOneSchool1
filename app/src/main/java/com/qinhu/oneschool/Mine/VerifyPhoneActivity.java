package com.qinhu.oneschool.Mine;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class VerifyPhoneActivity extends AppCompatActivity {
    private ImageView backbtn;
    private TextView phone;
    private TextView err;
    private EditText code;
    private ShapeCornerBgView codebtn;
    private EditText password;
    private EditText repassword;
    private ShapeCornerBgView submitbtn;
    private MyUser myUser;
    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        StatusBarUtil.WhiteBar(VerifyPhoneActivity.this);

        initView();
        initEvent();

    }

    private void initView() {
        backbtn = findViewById(R.id.id_mheader_backbtn);
        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("短信验证");
        phone = findViewById(R.id.id_verify_phone);
        err = findViewById(R.id.id_verify_err);
        code = findViewById(R.id.id_verify_code);
        codebtn = findViewById(R.id.id_verify_codebtn);
        password = findViewById(R.id.id_verify_password);
        repassword = findViewById(R.id.id_verify_repassword);
        submitbtn = findViewById(R.id.id_verify_submitbtn);
        myUser = BmobUser.getCurrentUser(MyUser.class);
        String phoneNumber = myUser.getUsername().substring(0, 3) + "****" + myUser.getUsername().substring(7, phone.length());
        phone.setText(phoneNumber);
        codebtn.setBgColor(getResources().getColor(R.color.tableorange));
        submitbtn.setBgColor(getResources().getColor(R.color.tableorange));
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.no_move,R.anim.search_out);
            }
        });
        codebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            BmobSMS.requestSMSCode(myUser.getUsername(),"OneSchool", new QueryListener<Integer>() {
                @Override
                public void done(Integer smsId,BmobException ex) {
                    if(ex==null){//验证码发送成功
                        time.start();//开始计时
                        Toast.makeText(VerifyPhoneActivity.this,"短信发送成功！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strPassword = password.getText().toString();
                final String strrePassword = repassword.getText().toString();
                final String strCode = code.getText().toString();
                if (strCode.isEmpty()){
                    err.setText("*验证码不能为空！");
                }else if (strPassword.isEmpty()){
                    err.setText("*密码不能为空！！");
                }else if (strrePassword.isEmpty()) {
                    err.setText("*请确认密码！");
                }else if(strPassword.equals(strrePassword.toString())){
                    BmobUser.resetPasswordBySMSCode(strCode,strPassword, new UpdateListener() {
                        @Override
                        public void done(BmobException ex) {
                            if(ex==null){
                                Toast.makeText(VerifyPhoneActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                err.setText(ex.toString());
                            }
                        }
                    });
                }else {
                    err.setText("*两次密码输入不相同！");
                }
            }
        });

    }


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            codebtn.setClickable(false);
            codebtn.setText(millisUntilFinished / 1000 + "秒");
        }
        @Override
        public void onFinish() {
            codebtn.setText("重新验证");
            codebtn.setClickable(true);
        }
    }

    public boolean onKeyDown(int keyCoder,KeyEvent event){
        if(keyCoder == KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.no_move,R.anim.search_out);
            return false;
        }
        return false;
    }

}
