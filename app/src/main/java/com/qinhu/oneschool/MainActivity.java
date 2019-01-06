package com.qinhu.oneschool;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.DB.AutoBackground;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Fragments.HomeFragment;
import com.qinhu.oneschool.Fragments.MessageFragment;
import com.qinhu.oneschool.Fragments.MineFragment;
import com.qinhu.oneschool.Fragments.StudyFragment;
import com.qinhu.oneschool.Im.MyConversationClickListener;
import com.qinhu.oneschool.Im.MyConversationListBehaviorListener;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyClass.Update;
import com.qinhu.oneschool.Public.TodayActivity;
import com.qinhu.oneschool.Service.UpdateDateService;
import com.qinhu.oneschool.Utils.FormatTime;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;


public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private LinearLayout home_tab;
    private LinearLayout chat_tab;
    private LinearLayout study_tab;
    private LinearLayout mine_tab;

    private ImageView id_home_icon;
    private ImageView id_chat_icon;
    private ImageView id_study_icon;
    private ImageView id_mine_icon;

    private TextView id_home_text;
    private TextView id_chat_text;
    private TextView id_study_text;
    private TextView id_mine_text;

    private Fragment homeFragment;
    private Fragment messageFragment;
    private Fragment studyFragment;
    private Fragment meFragment;

    private MyUser myuser;

    //定义自动更新服务
    private UpdateDateService.UpdateTime updateTime;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务已经链接
            updateTime=(UpdateDateService.UpdateTime)iBinder;
            updateTime.Update();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服务中断时
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据

        final String username = sharedPreferences.getString("username",null);
        final String password = sharedPreferences.getString("password",null);
        final String mToken = sharedPreferences.getString("token",null);

        if (!sharedPreferences.getBoolean("isFirst",true)){
            MyUser user= BmobUser.getCurrentUser(MyUser.class);
            if(user==null||user.getUsername()==null||user.getNickname()==null|| user.getAvatar()==null){
                MyUser.loginByAccount(username, password,new LogInListener<MyUser>() {
                    @Override
                    public void done(final MyUser user, BmobException e) {
                        if(e == null){
                            final String token= user.getToken();
                            connectIM(token);
                        }
                    }
                });
            }else {
                connectIM(mToken);
            }

            if (sharedPreferences.getBoolean("isTodayLaunch",true)){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, TodayActivity.class));
                    }
                });
            }else {
                startActivity(new Intent(MainActivity.this, LaunchActivity.class));
            }
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putBoolean("isFirst",false);
            editor.commit();//提交修改
            connectIM(mToken);
        }
        setContentView(R.layout.activity_main);
        CommonAction.getInstance().addActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(MainActivity.this,R.color.noColor);
        }

        RongIM.setConversationListBehaviorListener(new MyConversationListBehaviorListener());
        RongIM.setConversationClickListener(new MyConversationClickListener());

        Intent intent=new Intent(MainActivity.this,UpdateDateService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

        initView();
        initEvent();
        update_background();
        Update update = new Update();
        update.Update(MainActivity.this);
    }

    private void connectIM(final String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Toast.makeText(getApplicationContext(),""+"token错误",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String userid) {
                Toast.makeText(MainActivity.this,"IM连接成功",Toast.LENGTH_SHORT).show();
                myuser= BmobUser.getCurrentUser(MyUser.class);
                RongIM.getInstance().setCurrentUserInfo(new UserInfo(myuser.getUsername(),myuser.getNickname(), Uri.parse(myuser.getAvatar())));
                Intent intent=new Intent(MainActivity.this,UpdateDateService.class);
                bindService(intent,connection,BIND_AUTO_CREATE);
                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                    @Override
                    public UserInfo getUserInfo(String userId) {
                        return getUserInfoByLitepal(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
                    }
                }, false);
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
    }

    public UserInfo getUserInfoByLitepal(String s) {
        List<LoaclUser> list= DataSupport.where("account=? and username =?",myuser.getUsername(),s).find(LoaclUser.class);
        if(list.size()!=0){
            LoaclUser loaclUser=list.get(0);
            if(loaclUser.getBeizhu()!=null&&!loaclUser.getBeizhu().equals("")) {
                return new UserInfo(s, loaclUser.getBeizhu(), Uri.parse(loaclUser.getAvatar()));
            }else{
                return new UserInfo(s, loaclUser.getNickname(), Uri.parse(loaclUser.getAvatar()));
            }
        }
        return null;
    }

    private void update_background(){
        final SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        if (!preferences.contains("update_date")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("update_date", FormatTime.Current_Date());
            editor.commit();
        }else if(!preferences.getString("update_date",null).equals(FormatTime.Current_Date())){
            BmobQuery<AutoBackground> autoBackgroundBmobQuery = new BmobQuery<>();
            autoBackgroundBmobQuery.addWhereContains("update",FormatTime.Tomorrow_Date());
            autoBackgroundBmobQuery.findObjects(new FindListener<AutoBackground>() {
                @Override
                public void done(List<AutoBackground> list, BmobException e) {
                    if (e == null){
                        if (list.size()>0){
                            BmobFile bmobfile =new BmobFile("today.png","",list.get(0).getImgPath());
                            downloadFile(bmobfile);
                        }else {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("update_date",FormatTime.Current_Date());
                            editor.commit();
                        }
                    }
                }
            });
        }
    }

    private void downloadFile(BmobFile file){
        file.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("imagePath"+ FormatTime.Tomorrow_Date(),s);
                    editor.putString("update_date",FormatTime.Current_Date());
                    editor.commit();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {
            }
        });
    }

    private void initView() {
        home_tab = (LinearLayout) findViewById(R.id.home_tab);
        chat_tab = (LinearLayout) findViewById(R.id.chat_tab);
        study_tab = (LinearLayout) findViewById(R.id.study_tab);
        mine_tab = (LinearLayout) findViewById(R.id.mine_tab);

        id_home_icon = (ImageView) findViewById(R.id.id_home_icon);
        id_chat_icon = (ImageView)findViewById(R.id.id_chat_icon);
        id_study_icon = (ImageView)findViewById(R.id.id_study_icon);
        id_mine_icon = (ImageView) findViewById(R.id.id_mine_icon);

        id_home_text = (TextView) findViewById(R.id.id_home_text);
        id_chat_text = (TextView)findViewById(R.id.id_chat_text);
        id_study_text = (TextView)findViewById(R.id.id_study_text);
        id_mine_text = (TextView) findViewById(R.id.id_mine_text);
        select(0);

//        Intent intent=new Intent(MainActivity.this,UpdateDateService.class);
//        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void initEvent() {
        home_tab.setOnClickListener(this);
        chat_tab.setOnClickListener(this);
        study_tab.setOnClickListener(this);
        mine_tab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_tab:
                select(0);
                break;
            case R.id.chat_tab:
                select(1);
                break;
            case R.id.study_tab:
                select(2);
                break;
            case R.id.mine_tab:
                select(3);
                break;
            default:
                break;
        }
    }

    private void hindFragment(FragmentTransaction transaction) {
        if (homeFragment != null){
            transaction.hide(homeFragment);
        }
        if (messageFragment != null){
            transaction.hide(messageFragment);
        }
        if (studyFragment != null){
            transaction.hide(studyFragment);
        }
        if (meFragment != null){
            transaction.hide(meFragment);
        }
    }

    public void select(int i){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hindFragment(transaction);
        resetImg();
        switch (i){
            case 0:
                if (homeFragment == null){
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.id_content,homeFragment);
                }else {
                    transaction.show(homeFragment);
                }
                id_home_icon.setImageResource(R.drawable.home_a);
                id_home_text.setTextColor(getResources().getColor(R.color.selected));
                break;
            case 1:
                if (messageFragment == null){
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.id_content,messageFragment);
                }else {
                    transaction.show(messageFragment);
                }
                id_chat_icon.setImageResource(R.drawable.message_a);
                id_chat_text.setTextColor(getResources().getColor(R.color.selected));
                break;
            case 2:
                if (studyFragment == null){
                    studyFragment = new StudyFragment();
                    transaction.add(R.id.id_content,studyFragment);
                }else {
                    transaction.show(studyFragment);
                }
                id_study_icon.setImageResource(R.drawable.study_a);
                id_study_text.setTextColor(getResources().getColor(R.color.selected));
                break;
            case 3:
                if (meFragment == null){
                    meFragment = new MineFragment();
                    transaction.add(R.id.id_content,meFragment);
                }else {
                    transaction.show(meFragment);
                }
                id_mine_icon.setImageResource(R.drawable.mine_a);
                id_mine_text.setTextColor(getResources().getColor(R.color.selected));
                break;
            default:
                break;
        }

        transaction.commit();
    }

    private void resetImg() {
        id_home_icon.setImageResource(R.drawable.home_b);
        id_chat_icon.setImageResource(R.drawable.message_b);
        id_study_icon.setImageResource(R.drawable.study_b);
        id_mine_icon.setImageResource(R.drawable.mine_b);

        id_home_text.setTextColor(getResources().getColor(R.color.no_selected));
        id_chat_text.setTextColor(getResources().getColor(R.color.no_selected));
        id_study_text.setTextColor(getResources().getColor(R.color.no_selected));
        id_mine_text.setTextColor(getResources().getColor(R.color.no_selected));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIM.getInstance().disconnect();
    }
}
