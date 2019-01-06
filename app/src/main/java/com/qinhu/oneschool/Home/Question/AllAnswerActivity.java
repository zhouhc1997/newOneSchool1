package com.qinhu.oneschool.Home.Question;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liji.imagezoom.util.ImageZoom;
import com.qinhu.oneschool.DB.Answer;
import com.qinhu.oneschool.DB.Question;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.Public.WebViewActivity;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.PixelUtil;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.qinhu.oneschool.Utils.FormatTime.StringToYearMonthDay;


public class AllAnswerActivity extends AppCompatActivity {

    private ImageView backbtn;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_answer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(AllAnswerActivity.this,R.color.noColor); }

        initView();
        initEvent();

    }

    private void initView() {
        backbtn = findViewById(R.id.id_allanswer_backbtn);
        recyclerView = findViewById(R.id.id_allanswer_recyclerview);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(AllAnswerActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager1);
        final BmobQuery<Answer> bmobQuery = new BmobQuery<>();
        bmobQuery.include("user");
        bmobQuery.setLimit(5);
        bmobQuery.addWhereEqualTo("question",getIntent().getStringExtra("questionId"));
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<Answer>() {
            @Override
            public void done(final List<Answer> list, BmobException e) {
                if (e==null){
                    adapter = new HomeAdapter( R.layout.answerlistlayout,list);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class HomeAdapter extends BaseQuickAdapter<Answer, BaseViewHolder> {
        public HomeAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final Answer item) {
            helper.setText(R.id.id_answerlist_nickname,item.getUser().getNickname())
                    .setText(R.id.id_answerlist_time,StringToYearMonthDay(item.getCreatedAt()));

            CircleImageView avatar = helper.getView(R.id.id_answerlist_avatar);
            Glide.with(AllAnswerActivity.this)
                    .load(item.getUser().getAvatar())
                    .into(avatar);

            final ShapeCornerBgView agreebtn = helper.getView(R.id.id_answerlist_agree);
            agreebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = new Question();
                    question.setAgree(item);
                    question.update(getIntent().getStringExtra("questionId"), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if ( e== null){
                                Toast.makeText(AllAnswerActivity.this,"已采纳",Toast.LENGTH_SHORT).show();
                                agreebtn.setClickable(false);
                                agreebtn.setText("已采纳");
                                agreebtn.setBgColor(getResources().getColor(R.color.no_selected));
                            }
                        }
                    });
                }
            });


            LinearLayout linearLayout = helper.getView(R.id.id_answerlist_box);

            Document doc = Jsoup.parse(item.getContent());
            Element content = doc.getElementById("a");
            Elements links = content.children();

            for (Element link : links) {
                Log.d("RichEditor", link.nodeName());
                if (link.nodeName().equals("div")){
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LayoutInflater inflater1 = LayoutInflater.from(AllAnswerActivity.this);
                    View view = inflater1.inflate(R.layout.answer_text, null);
                    TextView textView = view.findViewById(R.id.id_answer_text);
                    textView.setText(link.text());
                    CharSequence text = textView.getText();
                    if (text instanceof Spannable) {
                        int end = text.length();
                        Spannable sp = (Spannable) text;
                        URLSpan urls[] = sp.getSpans(0, end, URLSpan.class);
                        SpannableStringBuilder style = new SpannableStringBuilder(text);
                        style.clearSpans();
                        for (URLSpan urlSpan : urls) {
                            MyURLSpan myURLSpan = new MyURLSpan(urlSpan.getURL());
                            style.setSpan(myURLSpan, sp.getSpanStart(urlSpan),
                                    sp.getSpanEnd(urlSpan),
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                        textView.setText(style);
                    }
                    view.setLayoutParams(lp);
                    linearLayout.addView(view);
                }else {
                    ImageView imageView = new ImageView(AllAnswerActivity.this);
                    imageView.setPadding(0,0,0, PixelUtil.dp2px(10,AllAnswerActivity.this));
                    Glide.with(AllAnswerActivity.this)
                            .load(link.attr("src"))
                            .into(imageView);
                    final List<String> list = new ArrayList<>();
                    list.add(0,link.attr("src"));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageZoom.show(AllAnswerActivity.this,0,list);
                        }
                    });
                    linearLayout.addView(imageView);
                }
            }
        }
    }

    private class MyURLSpan extends ClickableSpan {
        private String url;
        public MyURLSpan(String url) {
            this.url = url;
        }
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(AllAnswerActivity.this,WebViewActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        }
    }




}
