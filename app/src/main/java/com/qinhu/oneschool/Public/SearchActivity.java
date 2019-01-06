package com.qinhu.oneschool.Public;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qinhu.oneschool.DB.Question;
import com.qinhu.oneschool.Home.Question.QuestionActivity;
import com.qinhu.oneschool.Home.Question.QuestionDetailActivity;
import com.qinhu.oneschool.MyClass.DeletableEditText;
import com.qinhu.oneschool.MyClass.SignKeyWordTextView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.qinhu.oneschool.Utils.FormatTime.StringToYearMonthDay;


public class SearchActivity extends AppCompatActivity {
    private int type;

    private EditText mEditText;
    private TextView backbtn;
    private ImageView clearbtn;

    private RecyclerView mRecyclerView;
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(SearchActivity.this,R.color.noColor);
        }

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);

        initView();
        initEvent();

    }


    private void initView() {
        mEditText = findViewById(R.id.id_search_edit);
        backbtn = findViewById(R.id.id_search_backbtn);
        clearbtn = findViewById(R.id.id_search_clear);
        mRecyclerView = findViewById(R.id.id_search_recyclerview);
        LinearLayoutManager layoutManager1=new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager1);
    }

    private void initEvent() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.no_move,R.anim.search_out);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditText.getText().length() > 0){
                    clearbtn.setVisibility(View.VISIBLE);
                }else {
                    clearbtn.setVisibility(View.GONE);
                }
                startSearch(mEditText.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });

    }

    private void startSearch(String s) {
        switch (type){
            case 0:
                if (s.isEmpty()){
                    List<Question> list = new ArrayList<>();
                    questionAdapter = new QuestionAdapter(R.layout.search_question,list);
                    mRecyclerView.setAdapter(questionAdapter);
                    break;
                }
                final BmobQuery<Question> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereContains("content", s);
                bmobQuery.addQueryKeys("objectId,content");
                bmobQuery.findObjects(new FindListener<Question>() {
                    @Override
                    public void done(final List<Question> list, BmobException e) {
                        if (e == null){
                            questionAdapter = new QuestionAdapter(R.layout.search_question,list);
                            questionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    Intent intent = new Intent(SearchActivity.this, QuestionDetailActivity.class);
                                    intent.putExtra("questionId",list.get(position).getObjectId());
                                    intent.putExtra("question",list.get(position).getContent());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            mRecyclerView.setAdapter(questionAdapter);
                        }
                    }
                });
                break;
        }
    }

    private class QuestionAdapter extends BaseQuickAdapter<Question, BaseViewHolder> {
        public QuestionAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder helper, Question item) {
            SignKeyWordTextView editText = helper.getView(R.id.id_searchlayout_edit);
            editText.setSignText(mEditText.getText().toString());
            editText.setText(item.getContent(), TextView.BufferType.NORMAL);
        }
    }


    public boolean onKeyDown(int keyCoder,KeyEvent event){
        if(keyCoder == KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.no_move,R.anim.search_out);
            return false;
        }
        return false;
    }






}
