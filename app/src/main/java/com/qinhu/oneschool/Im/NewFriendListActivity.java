package com.qinhu.oneschool.Im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.qinhu.oneschool.Adaptor.FriendManagerAdapter;
import com.qinhu.oneschool.DB.FriendManager;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.MyApplication;
import com.qinhu.oneschool.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class NewFriendListActivity extends AppCompatActivity {

    private List<FriendManager> list_friend;
    private FriendManagerAdapter adapter;


    private ListView listView;
    private TextView textView;
    private TextView textViewadd;


    private MyUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendmanager);
        user= BmobUser.getCurrentUser(MyUser.class);
        listView=(ListView)findViewById(R.id.shiplistview);
        textView=(TextView)findViewById(R.id.isData) ;
        textViewadd=(TextView)findViewById(R.id.textViewadd);
        textViewadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewFriendListActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });



        BmobQuery<FriendManager> query=new BmobQuery<FriendManager>();
        query.addWhereEqualTo("getAccountSend",user.getUsername());
        query.setLimit(50);
        query.order("-updateAt").findObjects(new FindListener<FriendManager>() {
            @Override
            public void done(List<FriendManager> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        textView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }else{
                        list_friend=list;
                        adapter=new FriendManagerAdapter(MyApplication.getContext(),list_friend);
                        listView.setAdapter(adapter);
                    }
                }else{

                }
            }
        });
        Intent intent=getIntent();
        String flag=intent.getStringExtra("flag");
        if(flag!=null&&flag.equals("yes")){
            //无须更新信息
        }else {
            //将所有消息标记为已读
            BmobQuery<FriendManager> query1 = new BmobQuery<>();
            query1.addWhereEqualTo("getAccountSend", user.getUsername());
            query1.addWhereEqualTo("isAndroid", false);
            query1.findObjects(new FindListener<FriendManager>() {
                @Override
                public void done(List<FriendManager> list, BmobException e) {
                    if (e == null) {
                        if (list.size() == 0) {
                            //什么都不做
                        } else {
                            for (int i = 0; i < list.size(); i++) {
                                FriendManager friendManager = new FriendManager();
                                friendManager.setIsAndroid(true);

                                friendManager.update(list.get(i).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {

                                        } else {

                                        }
                                    }
                                });
                            }
                        }
                    } else {

                    }
                }
            });
        }


    }

}
