package com.qinhu.oneschool.Home.Question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qinhu.oneschool.DB.Question;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.Public.SearchActivity;
import com.qinhu.oneschool.R;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.qinhu.oneschool.Utils.FormatTime.StringToYearMonthDay;

public class QuestionActivity extends AppCompatActivity {

    private ImageView backbtn;
    private ShapeCornerBgView searchbtn;
    private LinearLayout punishbtn;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();
        initEvent();

    }

    private void initView() {
        backbtn = findViewById(R.id.id_question_backbtn);
        searchbtn = findViewById(R.id.id_question_searchbtn);
        recyclerView = findViewById(R.id.id_question_recyclerview);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(QuestionActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager1);
        punishbtn = findViewById(R.id.id_question_punishbtn);

        final BmobQuery<Question> bmobQuery = new BmobQuery<>();
        bmobQuery.include("user");
        bmobQuery.setLimit(5);
        bmobQuery.order("-createdAt");
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = bmobQuery.hasCachedResult(Question.class);
        if(isCache){
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);   // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }

        bmobQuery.findObjects(new FindListener<Question>() {
            @Override
            public void done(final List<Question> list, BmobException e) {
                if (e==null){
                    adapter = new HomeAdapter( R.layout.questionlistlayout,list);
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(QuestionActivity.this, QuestionDetailActivity.class);
                            intent.putExtra("questionId",list.get(position).getObjectId());
                            intent.putExtra("question",list.get(position).getContent());
                            if (list.get(position).getAgree() != null){
                                intent.putExtra("questionAgree",list.get(position).getAgree().getObjectId());
                            }else {
                                intent.putExtra("questionAgree","");
                            }
                            startActivity(intent);
                        }
                    });
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
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QuestionActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.search_in,R.anim.no_move);
            }
        });
        punishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionActivity.this, QuestionPunishActivity.class));
            }
        });

    }

    private class HomeAdapter extends BaseQuickAdapter<Question, BaseViewHolder> {
        public HomeAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Question item) {
          helper.setText(R.id.id_questionlist_content,item.getContent())
                .setText(R.id.id_questionlist_time,StringToYearMonthDay(item.getCreatedAt()))
                .setText(R.id.id_questionlist_likes,item.getLikes()+"")
                .setText(R.id.id_questionlist_comments,item.getComments()+"");

        }
    }



}
