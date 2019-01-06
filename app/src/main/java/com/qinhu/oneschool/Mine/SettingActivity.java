package com.qinhu.oneschool.Mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinhu.oneschool.CommonAction;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.LoginActivity;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.FileUtil;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import java.io.File;

public class SettingActivity extends Activity implements View.OnClickListener{

    private ShapeCornerBgView layoutbnt;
    private ImageView backbtn;
    private LinearLayout changepassword;
    private LinearLayout notify;
    private LinearLayout cachebox;
    private TextView cachesize;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        CommonAction.getInstance().addActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(SettingActivity.this, R.color.noColor); }

        initView();
        initEvent();

    }

    private void initView() {
        layoutbnt = (ShapeCornerBgView) findViewById(R.id.id_setting_layoutbnt);
        cachebox = (LinearLayout) findViewById(R.id.id_setting_cachebox);
        notify = (LinearLayout) findViewById(R.id.id_setting_notifybox);
        cachesize = (TextView) findViewById(R.id.id_setting_cachesize);
        backbtn = findViewById(R.id.id_mheader_backbtn);
        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("设置");
        changepassword = (LinearLayout) findViewById(R.id.id_setting_changepassword);

        caculateCacheSize();
    }

    private void initEvent() {
        layoutbnt.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        changepassword.setOnClickListener(this);
        cachebox.setOnClickListener(this);
        notify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_setting_layoutbnt:                //注销登录
                MyUser.logOut();
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("stuUsername", "");
                editor.commit();//提交修改
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                intent.putExtra("isLogOut",true);
                startActivity(intent);
                CommonAction.getInstance().OutSign();
                break;
            case R.id.id_setting_changepassword:
                startActivity(new Intent(SettingActivity.this,SafeActivity.class));
                break;
            case R.id.id_setting_cachebox:
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要清理缓存？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClearAllCache(SettingActivity.this);
                                Toast.makeText(SettingActivity.this, "清理完毕！", Toast.LENGTH_SHORT).show();
                                cachesize.setText("0 KB");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.id_mheader_backbtn:
                finish();
                break;
        }
    }



    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            File dir2 = getExternalCacheDir();//获取sd卡缓存
            fileSize += FileUtil.getDirSize(dir2);
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        cachesize.setText(cacheSize);
    }
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 清除所有缓存
     * @param context
     */
    public static void ClearAllCache(Context context){
        File dir1 = context.getCacheDir();
        deleteFile(dir1);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            File dir2 = context.getExternalCacheDir();//获取sd卡缓存
            deleteFile(dir2);
        }
    }

    /**
     * 删除文件或者文件夹
     * @param dir
     */
    private static void deleteFile(File dir){
        if(dir!=null && dir.isDirectory()){
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);  //迭代删除
            }
        }else if(dir != null && dir.isFile()){
            dir.delete();
        }
    }


}
