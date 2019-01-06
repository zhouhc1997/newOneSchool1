package com.qinhu.oneschool.Im;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.qinhu.oneschool.DB.Friend;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.R;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ResetBeizhuActivity extends AppCompatActivity {



    private EditText editText;
    private TextView submit;
    private ImageView imageView;
    private String Beizhu;//用于onBacnPress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_beizhu);
        editText=(EditText)findViewById(R.id.ed_beizhu);
        submit=(TextView)findViewById(R.id.tv_submit);
        imageView=(ImageView)findViewById(R.id.id_user_backbtn);
        final  String user=getIntent().getStringExtra("user");
        final String friendUser=getIntent().getStringExtra("friendUser");
        final String beizhu=getIntent().getStringExtra("beizhu");
        Beizhu=beizhu;//赋值给onBackPress
        editText.setText(beizhu);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().equals(beizhu)){//备注未修改
                    finish();
                }else{
                    BmobQuery<Friend> bmobQuery=new BmobQuery<Friend>();
                    bmobQuery.setLimit(1);
                    bmobQuery.addWhereEqualTo("user",user);
                    bmobQuery.addWhereEqualTo("friendUser",friendUser);
                    bmobQuery.findObjects(new FindListener<Friend>() {
                        @Override
                        public void done(List<Friend> list, BmobException e) {
                            if(e==null){
                                if(list.size()!=0){
                                    final Friend friend=new Friend();
                                    friend.setNickName(editText.getText().toString());
                                    friend.update(list.get(0).getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?",user,friendUser).find(LoaclUser.class);
                                                if(loaclUserList !=null&& loaclUserList.size()!=0){
                                                    LoaclUser loaclUser =new LoaclUser();
                                                    loaclUser.setBeizhu(editText.getText().toString());
                                                    loaclUser.updateAll("account=? and username=?",user,friendUser);
                                                    //RongIM.getInstance()
                                                    // .refreshUserInfoCache(new UserInfo(friendUser
                                                    // , editText.getText().toString()
                                                    // , Uri.parse(loaclUser.getAvatar())));
                                                }
                                                Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else{

                                            }
                                        }
                                    });
                                }
                            }else{

                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(!editText.getText().toString().equals(Beizhu)){
            new AlertDialog.Builder(ResetBeizhuActivity.this)
                    .setMessage("修改还未提交，确认退出吗?")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }else{
            finish();
        }

    }
}
