package com.qinhu.oneschool.Public;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.liji.imagezoom.util.ImageZoom;
import com.qinhu.oneschool.DB.Friend;
import com.qinhu.oneschool.DB.FriendManager;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Im.ResetBeizhuActivity;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MainActivity;
import com.qinhu.oneschool.Mine.ComplieActivity;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;



public class UserActivity extends Activity {
    private ImageView backbtn;
    private CircleImageView avatar;
    private EditText nickname;
    private EditText sign;
    private EditText email;
    private EditText sex;
    private EditText beizhu;

    private LinearLayout linearLayout;
    private LinearLayout line_beizhu;

    private MyUser user;
    private String friendUsername;//若不为好友界面，则此项为username

    private ShapeCornerBgView action;
    private ShapeCornerBgView sendMsg;
    private ShapeCornerBgView addordelete;

    private List<LoaclUser> loaclUserList;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(UserActivity.this,R.color.noColor); }

            initView();
            initEvent();
            initShow();

    }

    private void initShow() {
        //开始填充UI
        Intent intent=getIntent();
        type = intent.getStringExtra("type");

        final String account=intent.getStringExtra("account");
        friendUsername=account;

        if(account.equals(user.getUsername())){
            type="mine";
        }

        List<LoaclUser> list = DataSupport.where("username=?",account).find(LoaclUser.class);//搜索所有账户
        if(list.size()==0){//若本地不存在
            BmobQuery<MyUser> myUserBmobQuery=new BmobQuery<>();
            myUserBmobQuery.setLimit(1);
            myUserBmobQuery.addWhereEqualTo("username",account);
            myUserBmobQuery.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if(e==null){
                        if(list.size()!=0) {
                            final MyUser myUser=list.get(0);
                            nickname.setText(myUser.getNickname());
                            Glide.with(UserActivity.this).load(myUser.getAvatar()).into(avatar);
                            avatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<String> urllist = new ArrayList<>();
                                    urllist.add(myUser.getAvatar());
                                    ImageZoom.show(UserActivity.this, myUser.getAvatar(), urllist);
                                }
                            });
                            if(myUser.getSignturn()!=null&&!myUser.getSignturn().equals("")){
                                sign.setText(myUser.getSignturn());
                            }
                            if(myUser.getEmail()!=null&&!myUser.getEmail().equals("")){
                                email.setText(myUser.getEmail());
                            }
                            if(myUser.getSex()!=null&&!myUser.getSex().equals("")){
                                sex.setText(myUser.getSex());
                            }
                        }
                    }else{
                    }
                }
            });
        }else{
            final LoaclUser myUser=list.get(0);
            nickname.setText(myUser.getNickname());
            Glide.with(UserActivity.this).load(myUser.getAvatar()).into(avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> urllist = new ArrayList<>();
                    urllist.add(myUser.getAvatar());
                    ImageZoom.show(UserActivity.this, myUser.getAvatar(), urllist);
                }
            });

            if(myUser.getSignturn()!=null&&!myUser.getSignturn().equals("")){
                sign.setText(myUser.getSignturn());
            }else{
                if(type.equals("mine")){
                    sign.setText("点击设置签名");
                }
            }
            if(myUser.getEmail()!=null&&!myUser.getEmail().equals("")){
                email.setText(myUser.getEmail());
            }else{
                if(type.equals("mine")){
                    email.setText("点击设置邮箱");
                }
            }
            if(myUser.getSex()!=null&&!myUser.getSex().equals("")){
                sex.setText(myUser.getSex());
            }else{
                if(type.equals("mine")){
                    sex.setText("点击设置性别");
                }
            }
        }

        switch (type){
            case "friend":{
                if(action.getVisibility()==View.VISIBLE) {
                    action.setVisibility(View.GONE);
                }
                if(linearLayout.getVisibility()==View.GONE){
                    linearLayout.setVisibility(View.VISIBLE);
                }
                addordelete.setText("删除好友");
                addordelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除好友逻辑
                        new  AlertDialog.Builder(UserActivity.this)
                                .setMessage("确定删除该好友吗?删除后您不再接收来自此账号的消息")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        BmobQuery<Friend> query=new BmobQuery<>();
                                        query.addWhereEqualTo("user",user.getUsername());
                                        query.addWhereEqualTo("friendUser",account);
                                        query.setLimit(1);
                                        query.findObjects(new FindListener<Friend>() {
                                            @Override
                                            public void done(List<Friend> list, BmobException e) {
                                                if(e==null){
                                                    Friend friend=new Friend();
                                                    friend.setObjectId(list.get(0).getObjectId());
                                                    friend.delete(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                BmobQuery<Friend> query=new BmobQuery<>();
                                                                query.addWhereEqualTo("user",account);
                                                                query.addWhereEqualTo("friendUser",user.getUsername());
                                                                query.setLimit(1);
                                                                query.findObjects(new FindListener<Friend>() {
                                                                    @Override
                                                                    public void done(List<Friend> list, BmobException e) {
                                                                        if(e==null){
                                                                            Friend friend=new Friend();
                                                                            friend.setObjectId(list.get(0).getObjectId());
                                                                            friend.delete(new UpdateListener() {
                                                                                @Override
                                                                                public void done(BmobException e) {
                                                                                    Toast.makeText(UserActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                                                    DataSupport.deleteAll(LoaclUser.class,"account=? and username=?",
                                                                                            user.getUsername(),account);
                                                                                    //删除会话
                                                                                    RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, account, new RongIMClient.ResultCallback<Boolean>() {
                                                                                        @Override
                                                                                        public void onSuccess(Boolean aBoolean) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onError(RongIMClient.ErrorCode errorCode) {

                                                                                        }
                                                                                    });
                                                                                        /*
                                                                                        RongIM.getInstance().getBlacklistStatus(account, new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
                                                                                            @Override



                                                                                            public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
                                                                                                if(blacklistStatus== RongIMClient.BlacklistStatus.IN_BLACK_LIST){

                                                                                                }else{
                                                                                                    //不在黑名单,则加入黑名单
                                                                                                    RongIM.getInstance().addToBlacklist(account, new RongIMClient.OperationCallback() {
                                                                                                        @Override
                                                                                                        public void onSuccess() {

                                                                                                        }


                                                                                                        @Override
                                                                                                        public void onError(RongIMClient.ErrorCode errorCode) {

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                            @Override
                                                                                            public void onError(RongIMClient.ErrorCode e) {

                                                                                            }
                                                                                        });
                                                                                        */
                                                                                    startActivity(new Intent(UserActivity.this, MainActivity.class));
                                                                                }
                                                                            });
                                                                        }else{

                                                                        }
                                                                    }
                                                                });
                                                            }else{

                                                            }
                                                        }
                                                    });
                                                }else{

                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .setCancelable(false)
                                .show();

                    }
                });
                sendMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!beizhu.getText().toString().equals("")){
                            RongIM.getInstance().startPrivateChat(UserActivity.this, account,beizhu.getText().toString());
                        }else{
                            RongIM.getInstance().startPrivateChat(UserActivity.this, account,nickname.getText().toString());
                        }
                        finish();

                    }
                });

                line_beizhu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(UserActivity.this,ResetBeizhuActivity.class);
                        intent.putExtra("user",user.getUsername());
                        intent.putExtra("friendUser",account);
                        intent.putExtra("beizhu",beizhu.getText().toString());

                        startActivity(intent);
                    }
                });

                break;
            }
            case "mine":{

                line_beizhu.setVisibility(View.GONE);

                if(action.getVisibility()==View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                if(linearLayout.getVisibility()==View.VISIBLE){
                    linearLayout.setVisibility(View.GONE);
                }
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UserActivity.this, ComplieActivity.class));
                    }
                });
                break;
            }
            case "user":{
                line_beizhu.setVisibility(View.GONE);
                if(action.getVisibility()==View.VISIBLE) {
                    action.setVisibility(View.GONE);
                }
                if(linearLayout.getVisibility()==View.GONE){
                    linearLayout.setVisibility(View.VISIBLE);
                }
                addordelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //添加好友逻辑
                        FriendManager friendManager=new FriendManager();
                        friendManager.setIsAndroid(false);
                        friendManager.setAccountSend(user.getUsername());
                        friendManager.setGetAccountSend(account);
                        friendManager.setType("add");
                        friendManager.setGet(false);
                        friendManager.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e==null){
                                    Toast.makeText(UserActivity.this,"发送申请成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{

                                }
                            }
                        });
                    }
                });
                sendMsg.setVisibility(View.GONE);
                    /*
                    sendMsg.setText("临时会话");
                    sendMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RongIM.getInstance().startPrivateChat(BaseApplication.getContext(), account,username);
                        }
                    });
                    */
                break;
            }
        }
    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        backbtn = findViewById(R.id.id_user_backbtn);
        avatar = findViewById(R.id.id_user_avatar);
        nickname =(EditText) findViewById(R.id.id_user_nickname);
        sex=(EditText)findViewById(R.id.id_user_sex);
        sign =(EditText) findViewById(R.id.id_user_sign);
        beizhu=(EditText) findViewById(R.id.id_user_beizhu);
        email=(EditText)findViewById(R.id.id_user_email);
        action=findViewById(R.id.id_user_complie);
        sendMsg=findViewById(R.id.id_user_notfriend_chat);
        addordelete=findViewById(R.id.id_user_notfriend_add);
        user = BmobUser.getCurrentUser(MyUser.class);
        linearLayout=(LinearLayout)findViewById(R.id.id_user_notfriend);
        sex.setEnabled(false);
        beizhu.setEnabled(false);
        sign.setEnabled(false);
        email.setEnabled(false);
        nickname.setEnabled(false);
        line_beizhu=(LinearLayout)findViewById(R.id.line_beizhu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(line_beizhu.getVisibility()==View.VISIBLE){
            List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?", user.getUsername(),friendUsername).find(LoaclUser.class);
            if (loaclUserList != null && loaclUserList.size() != 0) {
                beizhu.setText(loaclUserList.get(0).getBeizhu());
            }
        }
        if (type.equals("mine")){
            initShow();
        }
    }


}