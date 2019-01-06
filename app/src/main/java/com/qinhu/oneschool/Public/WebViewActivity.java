package com.qinhu.oneschool.Public;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qinhu.oneschool.R;


public class WebViewActivity extends AppCompatActivity implements  View.OnClickListener{

    private TextView txtTitle;
    private WebView webView;
    private ProgressBar mProgressBar;

    private ImageView sharebtn;
    private ImageView backbtn;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        txtTitle = findViewById(R.id.id_webview_title);
        txtTitle.setOnClickListener(this);

        sharebtn = findViewById(R.id.id_webview_share);
        sharebtn.setOnClickListener(this);

        backbtn = findViewById(R.id.id_webview_backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressBar = findViewById(R.id.id_mProgressBar);
        webView = findViewById(R.id.id_webview);
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                txtTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
        };

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(intent.getBooleanExtra("isBigger", false)); //设置支持缩放
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webView.setWebChromeClient(wvcc);
        webView.loadUrl(url);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        };

        webView.setWebViewClient(wvc);
    }

    public boolean onKeyDown(int keyCoder,KeyEvent event){
        if(webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK){
            webView.goBack();
            return true;
         }else {
            finish();
        }
         return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.id_webview_share :
//                ShareFunction share = new ShareFunction(this, null);
//                share.setmTitle(txtTitle.getText().toString());
//                share.setmUrl(url);
//                share.setmContent("来自OneSchool的分享");
//                share.setmImgUrl("http://www.oneschool.com.cn/oneschoollogo.png");
//                share.setmContext(this);
//                share.show();
//                break;
        }
    }
}
