package com.qinhu.oneschool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Im.MyConversationClickListener;
import com.qinhu.oneschool.Im.MyConversationListBehaviorListener;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyClass.AndroidBug5497Workaround;
import com.qinhu.oneschool.Public.TodayActivity;
import com.qinhu.oneschool.Service.UpdateDateService;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import io.rong.imkit.RongIM;
import cn.bmob.v3.exception.BmobException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;


public class LoginActivity extends Activity implements View.OnClickListener{

    private TextView textView_enter_register,login_err;
    private Button loginbnt;
    private EditText editText_username,editText_password;
    private ImageView psd_closeOropen;
    private String username;

    private ProgressDialog progressDialog;

    private MyUser myuser;

    //定义自动更新服务
    private UpdateDateService.UpdateTime updateTime;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务已经链接
            updateTime=(UpdateDateService.UpdateTime)iBinder;
            updateTime.Update();
            progressDialog.dismiss();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服务中断时
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);

        if (sharedPreferences.getString("stuUsername",null)==null||sharedPreferences.getString("stuUsername",null)==""){
            if (!getIntent().getBooleanExtra("isLogOut",false)){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoginActivity.this,LaunchActivity.class));
                    }
                });
            }
            setContentView(R.layout.activity_login);
            StatusBarUtil.noColorBar(LoginActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putBoolean("isFirst",true);
            editor.commit();//提交修改
            initView();
            initEvent();
        }else {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            LoginActivity.this.finish();
        }

    }


    private void initEvent() {
        textView_enter_register.setOnClickListener(this);
        loginbnt.setOnClickListener(this);
        psd_closeOropen.setOnClickListener(this);
        editText_username.setText(username);
    }

    private void initView() {
        editText_username = (EditText) findViewById(R.id.id_login_username);
        editText_password = (EditText) findViewById(R.id.id_login_password);
        textView_enter_register = (TextView) findViewById(R.id.id_login_register);
        login_err = (TextView) findViewById(R.id.id_login_err);
        loginbnt = (Button) findViewById(R.id.id_loginbutton);
        psd_closeOropen = (ImageView)findViewById(R.id.id_login_eye);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_login_register:
                Intent intent = new Intent(LoginActivity.this,BeforeRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.id_loginbutton:
                ScaleAnimation scaleAnimation = new ScaleAnimation((float) 1.0,(float)0.90,(float)1.0,(float)0.90);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillBefore(true);
                v.startAnimation(scaleAnimation);
                login();
                break;
            case R.id.id_login_eye:
                if (editText_password.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    editText_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    psd_closeOropen.setImageResource(R.drawable.eye_open);
                }else {
                    editText_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    psd_closeOropen.setImageResource(R.drawable.eye_close);
                }
                break;
            default:break;
        }
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

    private void login() {
        final String username = editText_username.getText().toString();
        final String password = editText_password.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            login_err.setText("用户名或密码不能为空！");
        } else {
            loginbnt.setEnabled(false);
            final MyUser user = new MyUser();
            //此处替换为你的用户名
            user.setUsername(username);
            //此处替换为你的密码
            user.setPassword(password);
            user.login(new SaveListener<MyUser>() {
                @Override
                public void done(final MyUser bmobUser, BmobException e) {
                    if (e == null) {
                        loginbnt.setEnabled(true);
                        final String token=bmobUser.getToken();
                        RongIM.connect(token, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                Toast.makeText(getApplicationContext(),""+"token错误",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onSuccess(String userid) {
                                myuser= BmobUser.getCurrentUser(MyUser.class);
                                RongIM.getInstance().setCurrentUserInfo(new UserInfo(myuser.getUsername(),myuser.getNickname(), Uri.parse(myuser.getAvatar())));
                                //绑定服务
                                progressDialog=new ProgressDialog(LoginActivity.this);
                                progressDialog.setMessage("更新信息中...");
                                progressDialog.show();
                                Intent intent=new Intent(LoginActivity.this,UpdateDateService.class);
                                bindService(intent,connection,BIND_AUTO_CREATE);
                                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                    @Override
                                    public UserInfo getUserInfo(String userId) {
                                        return getUserInfoByLitepal(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
                                    }
                                }, false);
                                BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
                                bmobQuery.include("stu");
                                bmobQuery.getObject(bmobUser.getObjectId(), new QueryListener<MyUser>() {
                                    @Override
                                    public void done(MyUser user, BmobException e) {
                                        if (e == null) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据
                                            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                                            editor.putString("stuUsername", user.getStu().getXuehao());
                                            editor.putString("stuPassword", user.getStu().getPassword());
                                            editor.putString("token", user.getToken());
                                            editor.putString("username", user.getUsername());
                                            editor.putString("password", password);
                                            editor.commit();//提交修改

                                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            overridePendingTransition(R.anim.search_in,R.anim.search_out);
                                            LoginActivity.this.finish();
                                        }
                                    }
                                });
                            }
                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Toast.makeText(getApplicationContext(),""+errorCode.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        login_err.setText(e.getMessage());
                        loginbnt.setEnabled(true);
                    }
                }
            });


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            finish();
            System.exit(0);
            RongIM.getInstance().disconnect();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
