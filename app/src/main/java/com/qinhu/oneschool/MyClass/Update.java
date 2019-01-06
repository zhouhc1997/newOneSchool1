package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;

import com.qinhu.oneschool.DB.VersionUpdate;
import com.qinhu.oneschool.Service.UpdateService;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

import static android.content.Context.MODE_PRIVATE;

public class Update{

    private Context mContext;
    private BaseDialog dialog;
    private UpdateDialog updateDialog;

    private SharedPreferences preferences ;
    Boolean hasDownload = false;


    public void Update(Context context){
        this.mContext = context;
        preferences = mContext.getSharedPreferences("update",MODE_PRIVATE);
        if (preferences.contains("localPath")){
            String savePath =preferences.getString("localPath",null);
            hasDownload =fileIsExists(savePath);
        }
        init();
    }

    private void init() {
            if (hasDownload){
                updateDialog = new UpdateDialog.Builder(mContext)
                        .setTitle("升级到最新版本 "+ preferences.getString("newest_version","1.0"))
                        .setMessage(preferences.getString("update_log","无"))
                        .setPositiveButton("立即安装", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openFile(preferences.getString("localPath",null));
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                            }
                        }).create();
                updateDialog.show();
            }else {
                BmobQuery<VersionUpdate> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereGreaterThan("version_n",getVersionCode());
                bmobQuery.findObjects(new FindListener<VersionUpdate>() {
                    @Override
                    public void done(final List<VersionUpdate> list, BmobException e) {
                        if (e==null){
                            if (list.size()>0){
                                if (list.get(0).getMust()){
                                    dialog = new BaseDialog.Builder(mContext).setTitle("检测到新版本").setMessage("是否现在下载更新？")
                                            .setPositiveButton("现在更新", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    downloadFile(list.get(0).getPath());
                                                    dialog.dismiss();
                                                }
                                            }).setNegativeButton("退出", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    System.exit(0);
                                                }
                                            }).create();
                                    dialog.show();
                                }else {
                                    if (isWifi(mContext)){
                                        Intent intent = new Intent(mContext, UpdateService.class);
                                        intent.putExtra("url",list.get(0).getPath());
                                        mContext.startService(intent);

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("newest_version",list.get(0).getVersion());
                                        editor.putString("update_log",list.get(0).getUpdate_log());
                                        editor.commit();
                                    }
                                }
                            }
                        }
                    }
                });
            }
    }

    public void openFile(String filepath){
        Intent intent = new Intent(); // 这是比较流氓的方法，绕过7.0的文件权限检查
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
             StrictMode.setVmPolicy(builder.build());
        }

         File file = new File(filepath);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setAction(Intent.ACTION_VIEW);//动作，查看
         intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//设置类型
         mContext.startActivity(intent);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        if (strFile==null)
            return false;
        try {
            File f=new File(strFile);
            if(!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public int getVersionCode() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private void downloadFile(String url) {
        BmobFile bmobfile =new BmobFile("update.apk","",url);
        bmobfile.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("localPath",s);
                    editor.commit();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }


    /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }




}
