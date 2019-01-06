package com.qinhu.oneschool.Adaptor;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyApplication;


import java.util.List;

import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.R;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public class FriendListAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;

    private List<LoaclUser> list;

    public FriendListAdapter(Context context, List<LoaclUser> list) {
        this.context = context;
        this.list = list;
    }


    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<LoaclUser> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list == null)
            return null;

        if (position >= list.size())
            return null;

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final LoaclUser mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.frienduri);
            viewHolder.tvUserId = (TextView) convertView.findViewById(R.id.friend_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            String letterFirst = "#";
            if (!TextUtils.isEmpty(letterFirst)) {
                if (!isLetterDigitOrChinese(letterFirst)) {
                    letterFirst = "#";
                }else {
                    letterFirst = String.valueOf(letterFirst.toUpperCase().charAt(0));
                }
            }
            viewHolder.tvLetter.setText(letterFirst);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        if (mContent.isExitsDisplayName()) {
            viewHolder.tvTitle.setText(this.list.get(position).getBeizhu());
        } else {
            viewHolder.tvTitle.setText(this.list.get(position).getNickname());
        }
        if(list.get(position).getAvatar()!=null&&list.get(position).getAvatar().contains("http")) {
            Glide.with(MyApplication.getContext()).load(list.get(position).getAvatar()).into(viewHolder.mImageView);
        }else{
            viewHolder.mImageView.setImageResource(R.drawable.blackdown);
        }


        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = "#";
            char firstChar = sortStr.charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return "#".charAt(0);
    }


    final static class ViewHolder {
        /**
         * 首字母
         */
        TextView tvLetter;
        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 头像
         */
        CircleImageView mImageView;
        /**
         * userid
         */
        TextView tvUserId;
    }

    private boolean isLetterDigitOrChinese(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";//其他需要，直接修改正则表达式就好
        return str.matches(regex);
    }
}
