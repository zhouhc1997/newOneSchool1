package com.qinhu.oneschool.Home.Question;

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

import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.DB.Question;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class QuestionPunishActivity extends AppCompatActivity {
    private ImageView backbtn;
    private EditText content;
    private TextView submitbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_punish);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(QuestionPunishActivity.this,R.color.noColor); }


        initView();
        initEvent();

    }

    private void initView() {
        backbtn = findViewById(R.id.id_questionpunish_backbtn);
        submitbtn = (TextView)findViewById(R.id.id_questionpunish_submitbtn);
        content = findViewById(R.id.id_questionpunish_content);

    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(QuestionPunishActivity.this,s.length() + "",Toast.LENGTH_SHORT).show();
                if (s.length() > 0){
                    submitbtn.setClickable(true);
                }else {
                    submitbtn.setClickable(false);
                }
            }
        });


        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question = new Question();
                question.setContent(content.getText().toString());
                question.setUser(BmobUser.getCurrentUser(MyUser.class));
                question.setComments(0);
                question.setLikes(0);
                question.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            Toast.makeText(QuestionPunishActivity.this,"发布成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(QuestionPunishActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
