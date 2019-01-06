package com.qinhu.oneschool.Im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.DB.Friend;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Public.UserActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddActivity extends AppCompatActivity {


    private EditText editText;
    private TextView textView;
    private MyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        editText=(EditText)findViewById(R.id.etContent);
        textView=(TextView)findViewById(R.id.tvTitleRight);
        user= BmobUser.getCurrentUser(MyUser.class);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()==11) {
                    BmobQuery<MyUser> query = new BmobQuery<>();
                    query.addWhereEqualTo("username", editText.getText().toString());
                    query.setLimit(1);
                    query.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(final List<MyUser> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "不存在此用户或已被封禁", Toast.LENGTH_SHORT).show();
                                } else {
                                    //此处intent要改
                                    BmobQuery<Friend> query = new BmobQuery<>();
                                    query.addWhereEqualTo("user", user.getUsername());
                                    query.addWhereEqualTo("friendUser", editText.getText().toString());
                                    query.setLimit(1);
                                    query.findObjects(new FindListener<Friend>() {
                                        @Override
                                        public void done(List<Friend> list0, BmobException e) {
                                            if(e==null){
                                                Intent intent = new Intent(AddActivity.this, UserActivity.class);
                                                if(list0.size()==0){
                                                    intent.putExtra("type","user");
                                                }else{
                                                    intent.putExtra("type","friend");
                                                }
                                                //将所有信息传入
                                                intent.putExtra("account", list.get(0).getUsername());
                                                startActivity(intent);

                                            }else{

                                            }
                                        }
                                    });

                                }
                            } else {

                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "用户id输入规格有误", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
