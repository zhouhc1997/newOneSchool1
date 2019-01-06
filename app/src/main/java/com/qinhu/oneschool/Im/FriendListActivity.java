package com.qinhu.oneschool.Im;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.qinhu.oneschool.Adaptor.FriendListAdapter;
import com.qinhu.oneschool.DB.FriendManager;
import com.qinhu.oneschool.DB.Friend_list;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyApplication;
import com.qinhu.oneschool.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import io.rong.imkit.RongIM;


public class FriendListActivity extends Activity implements View.OnClickListener {


    private RelativeLayout re_newfriends;
    private RelativeLayout re_chatroom;
    private RelativeLayout publicservice;
    private List<Friend_list> friendList;
    private FriendListAdapter friendListAdapter;
    private ListView friendListView;
    private TextView mUnreadTextView;
    private TextView show_no_friend;

    private MyUser user;

    private List<LoaclUser> loaclUserList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        re_newfriends=(RelativeLayout)findViewById(R.id.re_newfriends);
        re_newfriends.setOnClickListener(this);
        re_chatroom=(RelativeLayout)findViewById(R.id.re_chatroom);
        re_chatroom.setOnClickListener(this);
        publicservice=(RelativeLayout)findViewById(R.id.publicservice);
        publicservice.setOnClickListener(this);
        friendListView=(ListView)findViewById(R.id.listview);
        mUnreadTextView=(TextView)findViewById(R.id.tv_unread);
        show_no_friend=(TextView)findViewById(R.id.show_no_friend);


        friendList=new ArrayList<>();
        user= BmobUser.getCurrentUser(MyUser.class);

        loaclUserList = DataSupport.where("account=? and username!=?",user.getUsername(),user.getUsername()).find(LoaclUser.class);
        if(loaclUserList.size()==0){
            friendListView.setVisibility(View.GONE);
            show_no_friend.setVisibility(View.VISIBLE);
        }else {
            friendListAdapter = new FriendListAdapter(MyApplication.getContext(), loaclUserList);
            friendListView.setAdapter(friendListAdapter);
        }

        BmobQuery<FriendManager> query1=new BmobQuery<>();
        query1.addWhereEqualTo("getAccountSend",user.getUsername());
        query1.addWhereEqualTo("isAndroid",false);
        query1.count(FriendManager.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    if(count==0){
                        mUnreadTextView.setVisibility(View.GONE);
                    }else{
                        mUnreadTextView.setVisibility(View.VISIBLE);
                        mUnreadTextView.setText(""+count);
                    }
                }else{

                }
            }
        });

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LoaclUser loaclUser = loaclUserList.get(i);

                if (loaclUser.getBeizhu() != null) {
                    RongIM.getInstance().startPrivateChat(FriendListActivity.this, loaclUser.getUsername(), loaclUser.getBeizhu());
                }else{
                    RongIM.getInstance().startPrivateChat(FriendListActivity.this, loaclUser.getUsername(), loaclUser.getNickname());
                }
                finish();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        List<LoaclUser> list= DataSupport.where("account=? and username!=?",user.getUsername(),user.getUsername()).find(LoaclUser.class);;
        if(!list.equals(loaclUserList)){
            friendListAdapter = new FriendListAdapter(MyApplication.getContext(), list);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    friendListView.setAdapter(friendListAdapter);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_newfriends:
                Intent intent = new Intent(FriendListActivity.this, NewFriendListActivity.class);

                if(mUnreadTextView.getVisibility()== View.GONE){
                    intent.putExtra("flag","yes");
                }else {
                    mUnreadTextView.setVisibility(View.GONE);
                }
                startActivity(intent);
                break;
            case R.id.re_chatroom:
                startActivity(new Intent(FriendListActivity.this, GroupListActivity.class));
                break;
            case R.id.publicservice:
                Intent intentPublic = new Intent(FriendListActivity.this, PublicServiceActivity.class);
                startActivity(intentPublic);
                break;

        }
    }




}
