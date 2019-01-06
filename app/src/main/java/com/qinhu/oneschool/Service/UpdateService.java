package com.qinhu.oneschool.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class UpdateService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        downloadFile(url);
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadFile(String url) {
        BmobFile bmobfile =new BmobFile("update.apk","",url);
        File saveFile = new File(Environment.getExternalStorageDirectory(), bmobfile.getFilename());
        bmobfile.download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    SharedPreferences preferences = getSharedPreferences("update",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("localPath",s);
                    editor.commit();
                    Log.d("aaaaaaaaaaaa","Service ---- localPath="+ s);
                    stopSelf();
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
