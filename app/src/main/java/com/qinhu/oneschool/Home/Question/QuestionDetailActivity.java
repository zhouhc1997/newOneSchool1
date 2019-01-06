package com.qinhu.oneschool.Home.Question;

import android.content.Intent;
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
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liji.imagezoom.util.ImageZoom;
import com.qinhu.oneschool.DB.Answer;
import com.qinhu.oneschool.DB.MyMultipleItem;
import com.qinhu.oneschool.DB.Question;
import com.qinhu.oneschool.Mine.ComplieActivity;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.MyClass.ObservableScrollView;
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


public class QuestionDetailActivity extends AppCompatActivity {
    private ImageView backbtn;
    private String content;
    private Intent intent1;
    private TextView header;
    private RecyclerView recyclerView;
    private List<MyMultipleItem> datas02;
    private MultipleItemAdapter adapter;
    private String questionAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        StatusBarUtil.noColorBar(QuestionDetailActivity.this);

        initView();
        initEvent();
    }

    private void initView() {
        backbtn = (ImageView) findViewById(R.id.id_questiondetail_backbtn);
        header = findViewById(R.id.id_questiondetail_header);

        intent1 = getIntent();
        content = intent1.getStringExtra("question");
        header.setText(intent1.getStringExtra("question"));
        questionAgree = intent1.getStringExtra("questionAgree");

        recyclerView = findViewById(R.id.id_questiondetail_recyclerview);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(QuestionDetailActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager1);

        final BmobQuery<Answer> bmobQuery = new BmobQuery<>();
        bmobQuery.include("user");
        bmobQuery.setLimit(5);
        bmobQuery.addWhereEqualTo("question",getIntent().getStringExtra("questionId"));
        bmobQuery.findObjects(new FindListener<Answer>() {
            @Override
            public void done(final List<Answer> list, BmobException e) {
                if (e==null) {
                    Toast.makeText(QuestionDetailActivity.this,questionAgree,Toast.LENGTH_SHORT).show();
                    datas02 = new ArrayList<>();
                    for (int i = 0,size = list.size(); i < size+2;i++){
                        if (i==0){
                            datas02.add(new MyMultipleItem(MyMultipleItem.FIRST_TYPE,null));
                        }else if (i==1){
                            datas02.add(new MyMultipleItem(MyMultipleItem.SECOND_TYPE, null));
                        }else {
                            datas02.add(new MyMultipleItem(MyMultipleItem.NORMAL_TYPE, list.get(i-2)));
                        }
//                        if (list.get(i).getObjectId().equals(questionAgree)){
//                            datas02.add(1,new MyMultipleItem(MyMultipleItem.SECOND_TYPE, list.get(i-2)));
//                        }
                    }
                    adapter = new MultipleItemAdapter(datas02);
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

    private class MultipleItemAdapter extends BaseMultiItemQuickAdapter<MyMultipleItem, BaseViewHolder> {
        public MultipleItemAdapter(List data) {
            super(data);
            //必须绑定type和layout的关系
            addItemType(MyMultipleItem.FIRST_TYPE, R.layout.question_first_layout);
            addItemType(MyMultipleItem.SECOND_TYPE, R.layout.has_agreelayout);
            addItemType(MyMultipleItem.NORMAL_TYPE, R.layout.answerlistlayout);
        }

        @Override
        protected void convert(BaseViewHolder helper,final MyMultipleItem item) {
            switch (helper.getItemViewType()) {
                case MyMultipleItem.FIRST_TYPE:
                    helper.setText(R.id.id_questiondetail_content,content);
                    LinearLayout linearLayout = helper.getView(R.id.id_questiondetail_write);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(QuestionDetailActivity.this,WriteAnswerActivity.class);
                            intent.putExtra("questionId",intent1.getStringExtra("questionId"));
                            startActivity(intent);
                        }
                    });
                    break;
                case MyMultipleItem.SECOND_TYPE:
                    if (item.getData()!= null){
                        LinearLayout tv = (LinearLayout) findViewById(R.id.id_questiondetail_answer);
                        CircleImageView avatar = findViewById(R.id.id_questiondetail_avatar);
                        Glide.with(QuestionDetailActivity.this)
                                .load(item.getData().getUser().getAvatar())
                                .into(avatar);
                        TextView nickname = findViewById(R.id.id_questiondetail_nickname);
                        nickname.setText(item.getData().getUser().getNickname());
                        TextView time = findViewById(R.id.id_questiondetail_time);
                        time.setText(StringToYearMonthDay(item.getData().getUser().getCreatedAt()));
                        LinearLayout money = findViewById(R.id.id_questiondetail_money);
                        money.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(QuestionDetailActivity.this,"赞赏功能即将开通！",Toast.LENGTH_SHORT).show();
                            }
                        });

                        addView(tv,item.getData().getContent());
                    }

                    break;
                case MyMultipleItem.NORMAL_TYPE:
                    helper.setText(R.id.id_answerlist_nickname,item.getData().getUser().getNickname())
                            .setText(R.id.id_answerlist_time,StringToYearMonthDay(item.getData().getCreatedAt()));
                    CircleImageView mAvatar = helper.getView(R.id.id_answerlist_avatar);
                    Glide.with(QuestionDetailActivity.this)
                            .load(item.getData().getUser().getAvatar())
                            .into(mAvatar);
                    final ShapeCornerBgView agreebtn = helper.getView(R.id.id_answerlist_agree);
                    agreebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Question question = new Question();
                        question.setAgree(item.getData());
                        question.update(getIntent().getStringExtra("questionId"), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if ( e== null){
                                    Toast.makeText(QuestionDetailActivity.this,"已采纳",Toast.LENGTH_SHORT).show();
                                    agreebtn.setClickable(false);
                                    agreebtn.setText("已采纳");
                                    agreebtn.setBgColor(getResources().getColor(R.color.no_selected));
                                }
                            }
                        });
                        }
                    });
                    LinearLayout linearLayout1 = helper.getView(R.id.id_answerlist_box);
                    addView(linearLayout1,item.getData().getContent());
                    break;
            }
        }
    }

    private void addView(LinearLayout tv, String str) {
        Document doc = Jsoup.parse(str);
        Element content = doc.getElementById("a");
        Elements links = content.children();
        for (Element link : links) {
            Log.d("RichEditor", link.nodeName());
            if (link.nodeName().equals("div")) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LayoutInflater inflater1 = LayoutInflater.from(QuestionDetailActivity.this);
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
                tv.addView(view);
            } else {
                ImageView imageView = new ImageView(QuestionDetailActivity.this);
                imageView.setPadding(0, 0, 0, PixelUtil.dp2px(10, QuestionDetailActivity.this));
                Glide.with(QuestionDetailActivity.this)
                        .load(link.attr("src"))
                        .into(imageView);
                final List<String> list = new ArrayList<>();
                list.add(0, link.attr("src"));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageZoom.show(QuestionDetailActivity.this, 0, list);
                    }
                });
                tv.addView(imageView);
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
            Intent intent = new Intent(QuestionDetailActivity.this,WebViewActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        }
    }

}
