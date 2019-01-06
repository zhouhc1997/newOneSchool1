package com.qinhu.oneschool.Home;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qinhu.oneschool.DB.SchoolNews;
import com.qinhu.oneschool.Public.WebViewActivity;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SchoolNewsActivity extends AppCompatActivity {

    private Handler handler;
    private List<SchoolNews> schoolNewslists = new ArrayList<>();
    private HomeAdapter homeAdapter;
    private SchoolNews schoolNews;
    private RecyclerView recyclerView;
    private int flag = 0;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_news);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(SchoolNewsActivity.this,R.color.noColor);
        }else {
            ((View)findViewById(R.id.id_mheader_top)).setVisibility(View.GONE);
        }

        handler = new Handler();
        recyclerView = findViewById(R.id.id_article_schoolrecyclerview);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager1);
        setUrl(page);

        ((ImageView)findViewById(R.id.id_schoolnews_backbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setUrl(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl=
                        "http://jwc.wust.edu.cn/1925/list"+ page+".htm";
                final String r= crawler(httpUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Show(r);
                    }
                });
            }
        }).start();
    }

    private void Show(String r) {
        org.jsoup.nodes.Document doc = Jsoup.parse(r);
        Elements element = doc.getElementsByClass("news");
        for (int i = 0,size=element.size();i<size;i++){
            String title = element.get(i).child(0).text();
            String time = element.get(i).child(1).text();
            Element link = element.get(i).child(0).select("a").first();
            String url = link.attr("href");
            schoolNews = new SchoolNews(title,time,url);
            schoolNewslists.add(schoolNews);
        }
        if (flag == 0){
            homeAdapter = new HomeAdapter(R.layout.schoolnewslist,schoolNewslists);
            recyclerView.setAdapter(homeAdapter);
        }else {
            homeAdapter.loadMoreComplete();
            homeAdapter.notifyDataSetChanged();
        }
        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView url = view.findViewById(R.id.id_article_schoolnewsurl);
                TextView title = view.findViewById(R.id.id_article_schoolnewstitle);
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url","http://jwc.wust.edu.cn"+url.getText().toString());
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("isBigger",true);
                startActivity(intent);
            }
        });
        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = page+1;
                        flag = 1;
                        setUrl(page);
                    }
                }, 500);
            }
        }, recyclerView);
    }

    public class HomeAdapter extends BaseQuickAdapter<SchoolNews, BaseViewHolder> {
        public HomeAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SchoolNews item) {
            helper.setText(R.id.id_article_schoolnewstitle, item.getTitle())
                    .setText(R.id.id_article_schoolnewsurl, item.getUrl())
                    .setText(R.id.id_article_schoolnewstime, item.getTime());
        }
    }

    public String crawler(String httpUrl) {
        String msg = ""; //服务器返回结果
        try {
            //创建URL对象
            URL url = new URL(httpUrl);
            //创建HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    url.openConnection();
            //设置连接相关属性
            httpURLConnection.setConnectTimeout(5000); //设置连接超时为5秒
            httpURLConnection.setRequestMethod("GET"); //设置请求方式(默认为get)
            httpURLConnection.setRequestProperty("Charset", "UTF-8"); //设置uft-8字符集
            //建立到连接(可省略)
            httpURLConnection.connect();
            //获得服务器反馈状态信息
            //200：表示成功完成(HTTP_OK)， 404：请求资源不存在(HTTP_NOT_FOUND)
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //接收服务器返回的信息（输入流）
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    msg+=line+"\n";
                }
//关闭缓冲区和连接
                bufferedReader.close();
                httpURLConnection.disconnect();
            }
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
