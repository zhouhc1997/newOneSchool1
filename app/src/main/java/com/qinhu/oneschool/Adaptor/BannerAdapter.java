package com.qinhu.oneschool.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qinhu.oneschool.R;

/**
 * Created by zhouhc on 2018/12/27.
 */

public class BannerAdapter extends PagerAdapter implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private Context mContext;
    private ItemClickListener itemClickListener;
    private int currentPosition = 0;
    private int[]images = new int[]{};  //模拟存放要展示的图片

    public BannerAdapter(Context context, ViewPager viewPager) {
        mContext = context;
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setImages(int[]images ){
        this.images = images;
    }

    @Override
    public int getCount() {
        return 10000;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_banner, null);
        ImageView imageView = view.findViewById(R.id.id_banner_img);
        Glide.with(mContext).load(images[position%images.length]).into(imageView);
        imageView.setOnClickListener(this);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v) {
        if (null != itemClickListener) {
            itemClickListener.onItemClick(currentPosition);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface ItemClickListener {
        void onItemClick(int index);
    }

}