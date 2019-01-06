package com.qinhu.oneschool.Mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SafeActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText oldpassword;
    private EditText newpassword;
    private EditText renewpassword;
    private TextView err;
    private TextView verifyphone;
    private ShapeCornerBgView submitbtn;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

        StatusBarUtil.WhiteBar(SafeActivity.this);

            initView();
            initEvent();
    }
    private void initView() {
        backbtn = findViewById(R.id.id_mheader_backbtn);
        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("修改密码");
        oldpassword = (EditText) findViewById(R.id.id_changepassword_oldpassword);
        newpassword = (EditText) findViewById(R.id.id_changepassword_password);
        renewpassword = (EditText) findViewById(R.id.id_changepassword_repassword);
        err = (TextView) findViewById(R.id.id_changepassword_err);
        verifyphone = (TextView) findViewById(R.id.id_safe_virefyphone);
        submitbtn = (ShapeCornerBgView) findViewById(R.id.id_changepassword_submitbtn);
        submitbtn.setBgColor(getResources().getColor(R.color.color_orange));
    }
    private void initEvent() {
        backbtn.setOnClickListener(this);
        submitbtn.setOnClickListener(this);
        verifyphone.setOnClickListener(this);
        oldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                err.setText("");
            }
        });

        newpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                err.setText("");
            }
        });

        renewpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                err.setText("");
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_mheader_backbtn:
                finish();
                break;
            case R.id.id_changepassword_submitbtn:
                changePassword();
                break;
            case R.id.id_safe_virefyphone:
                startActivity(new Intent(SafeActivity.this,VerifyPhoneActivity.class));
                overridePendingTransition(R.anim.search_in,R.anim.no_move);
                break;
             default:
                 break;
        }
    }

    private void changePassword() {
        if (oldpassword.getText().toString().isEmpty()){
            err.setText("*旧密码不能为空！");
        }else if (newpassword.getText().toString().isEmpty()){
            err.setText("*新密码不能为空！");
        }else if (renewpassword.getText().toString().isEmpty()) {
            err.setText("*请再次确认新密码！");
        }else  if (newpassword.getText().toString().equals(oldpassword.getText().toString())){
            err.setText("*新密码不能和旧密码相同！");
        }else if(newpassword.getText().toString().equals(renewpassword.getText().toString())){
            BmobUser.updateCurrentUserPassword(oldpassword.getText().toString(), newpassword.getText().toString(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Toast.makeText(SafeActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        if (e.getErrorCode() == 210){
                            err.setText("*旧密码不正确！");
                        }else {
                            Toast.makeText(SafeActivity.this,"这是一个人类无法理解的错误！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }else {
            err.setText("*两次密码输入不相同！");
        }
    }
}
