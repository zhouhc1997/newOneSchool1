package com.qinhu.oneschool.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

public class Tools {
    static ProgressDialog progressDialog;

    public static void ShowProgress(Context context,String s){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage(s);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public static void CloseProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    //自定义toast样式
    public static void warntoast(Context context,String s){
        MyToast myToast = MyToast.makeText(context,s, Toast.LENGTH_SHORT);
        myToast .setGravity(Gravity.CENTER,0,-60);
        myToast .show();
    }


    //检测网络是否连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                //mNetworkInfo.isAvailable();
                return true;//有网
            }
        }
        return false;//没有网
    }
}
