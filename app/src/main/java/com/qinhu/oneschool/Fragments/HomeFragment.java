package com.qinhu.oneschool.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;
import com.qinhu.oneschool.Adapter.BannerAdapter;
import com.qinhu.oneschool.DB.SchoolNews;
import com.qinhu.oneschool.Home.GradesActivity;
import com.qinhu.oneschool.Home.Money.MoneyActivity;
import com.qinhu.oneschool.Home.Question.QuestionActivity;
import com.qinhu.oneschool.Home.SchoolNewsActivity;
import com.qinhu.oneschool.Home.Timetable.TimetableActivity;
import com.qinhu.oneschool.MyClass.MyScrollView;
import com.qinhu.oneschool.Public.WebViewActivity;
import com.qinhu.oneschool.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private MyScrollView myScrollView;
    private RelativeLayout header;

    private ViewFlipper viewFlipper;
    private ImageView headerimg;
    private ImageView headerlogo;
    private TextView headername;

    private LinearLayout grades;
    private LinearLayout question;
    private LinearLayout timetable;
    private LinearLayout money;
    private LinearLayout vote;

    private TextView school;
    private LinearLayout schoolbox;
    private BannerAdapter mBannerAdapter;
    private ViewPager mViewpager;

    private ViewPagerIndicator mViewPagerIndicator;

    private Timer mTimer = null;

    private static Handler mHandler = null;

    private static final int UPDATE_TEXTVIEW = 0;
    private boolean isPause = false;
    private TimerTask mTimerTask = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initEvent();
        initFirstBanner();
    }

    private void initView() {
        myScrollView = getView().findViewById(R.id.id_homefragment_scrollview);
        header = getView().findViewById(R.id.id_home_headerbox);
        headerimg = getView().findViewById(R.id.id_home_headerimg);
        headerlogo = getView().findViewById(R.id.id_home_headerlogo);
        headername = getView().findViewById(R.id.id_home_headername);
        school = getView().findViewById(R.id.id_home_school);
        schoolbox = getView().findViewById(R.id.id_home_schoolbox);
        viewFlipper = getView().findViewById(R.id.filpper);
        mViewpager = getView().findViewById(R.id.id_home_viewpager);
        mViewPagerIndicator = (ViewPagerIndicator) getView().findViewById(R.id.indicator_line);

        grades = getView().findViewById(R.id.id_home_grades);
        question = getView().findViewById(R.id.id_home_question);
        timetable = getView().findViewById(R.id.id_home_timetable);
        money = getView().findViewById(R.id.id_home_money);
        vote = getView().findViewById(R.id.id_home_vote);

        BmobQuery<SchoolNews> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(4);
        bmobQuery.order("-createdAt");
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = bmobQuery.hasCachedResult(SchoolNews.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);   // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        bmobQuery.findObjects(new FindListener<SchoolNews>() {
            @Override
            public void done(List<SchoolNews> list, BmobException e) {
                if (e == null) {
                    for (int i = 0, size = list.size(); i < size; i++) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.viewflipper, null);
                        TextView textView = view.findViewById(R.id.id_viewflipper_text);
                        textView.setText(list.get(i).getTitle());
                        viewFlipper.addView(view);
                    }
                }
            }
        });

    }

    private void initEvent() {
        myScrollView.setOnScollChangedListener(new MyScrollView.OnScollChangedListener() {
            @Override
            public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                header.setVisibility(View.VISIBLE);
                if (y==0){
                    header.setVisibility(View.GONE);
                }else {
                    int yy = y;
                    if (yy > 255) {
                        yy = 255;
                    }
                    headerimg.setImageAlpha(yy);
                    headername.setTextColor(Color.argb(yy, 255, 255, 255));
                    headerlogo.getDrawable().setAlpha(yy);
                }
            }
        });

        grades.setOnClickListener(this);
        schoolbox.setOnClickListener(this);
        school.setOnClickListener(this);
        question.setOnClickListener(this);
        timetable.setOnClickListener(this);
        money.setOnClickListener(this);
        vote.setOnClickListener(this);
        viewFlipper.setOnClickListener(this);
    }


    private void initFirstBanner() {
        final int[] images = new int[]{ R.drawable.test, R.drawable.test1,R.drawable.test, R.drawable.test1};  //模拟存放要展示的图片
        mBannerAdapter = new BannerAdapter(getApplicationContext(), mViewpager);
        mBannerAdapter.setImages(images);
        mViewpager.setAdapter(mBannerAdapter);
        mViewpager.setOffscreenPageLimit(2);//预加载2个
        mViewpager.setPageMargin(20);//设置viewpage之间的间距
        mViewpager.setClipChildren(false);
        mViewPagerIndicator.setViewPager(mViewpager, images.length);
        mBannerAdapter.setItemClickListener(new BannerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int index) {
                ToWebView("https://baike.baidu.com/item/%E6%AD%A6%E6%B1%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6%E6%98%9F%E8%BE%B0%E7%A4%BE/4030456?fr=aladdin");
            }
        });
        mViewpager.setCurrentItem(1000 * images.length);
        startTimer();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXTVIEW:
                        updateTextView();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        Log.d("aaaaaa", "onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        puseTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        reStartTimer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_home_grades:
                startActivity(new Intent(getContext(), GradesActivity.class));
                break;
            case R.id.id_home_timetable:
                startActivity(new Intent(getContext(), TimetableActivity.class));
                break;
            case R.id.id_home_money:
                startActivity(new Intent(getContext(), MoneyActivity.class));
                break;
            case R.id.id_home_school:
                ToWebView("http://www.oneschool.com.cn/WUST/Wust2.html");
                break;
            case R.id.id_home_schoolbox:
                ToWebView("http://www.oneschool.com.cn/WUST/Wust2.html");
                break;
            case R.id.id_home_question:
                startActivity(new Intent(getContext(), QuestionActivity.class));
                break;
            case R.id.id_home_vote:

                break;
            case R.id.filpper:
                startActivity(new Intent(getContext(), SchoolNewsActivity.class));
                break;
            default:
                break;
        }
    }

    private void ToWebView(String url) {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void updateTextView() {
        int curItem = mViewpager.getCurrentItem();
        mViewpager.setCurrentItem(curItem + 1);
    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sendMessage(UPDATE_TEXTVIEW);
                    do {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);
                }
            };
        }
        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 5000, 5000);
    }

    public void puseTimer(){
        isPause = true;
    }
    public void reStartTimer(){
        isPause = false;
    }
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void sendMessage(int id) {
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }


}
