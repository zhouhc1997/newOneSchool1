package com.qinhu.oneschool;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.v3.Bmob;
import io.rong.imkit.RongIM;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;


/**
 * Created by Administrator on 2018/12/7.
 */

public class MyApplication extends Application{


    public static Context baseContext;

    @Override
    public void onCreate() {
        super.onCreate();
        baseContext = getApplicationContext();

        LitePal.initialize(this);

        Bmob.initialize(this, "54c04d016b965aa7748f3f3ddd96ec1a");
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
        }
    }
    public static  Context getContext(){
        return baseContext;
    }

    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
