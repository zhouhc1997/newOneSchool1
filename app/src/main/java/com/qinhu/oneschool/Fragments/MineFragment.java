package com.qinhu.oneschool.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Mine.ComplieActivity;
import com.qinhu.oneschool.Mine.FeedbackActivity;
import com.qinhu.oneschool.Mine.SettingActivity;
import com.qinhu.oneschool.Mine.VersionActivity;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Public.UserActivity;

import cn.bmob.v3.BmobUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private CircleImageView avatar;
    private TextView nickname;

    private ImageView write;
    private ImageView setting;

    private RelativeLayout wallet;
    private RelativeLayout vip;
    private RelativeLayout mypunish;
    private RelativeLayout mycollection;
    private RelativeLayout device;
    private RelativeLayout update;
    private String avatarPath = "";

    private MyUser user;

    private Boolean isClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        avatar = getView().findViewById(R.id.id_mine_avatar);
        nickname = getView().findViewById(R.id.id_mine_nickname);
        user = BmobUser.getCurrentUser(MyUser.class);
        avatarPath = user.getAvatar();
        Glide.with(getContext()).load(avatarPath).into(avatar);
        nickname.setText(user.getNickname());
        nickname.setShadowLayer(6f,6f,6f,getResources().getColor(R.color.shape));

        write = getView().findViewById(R.id.id_mine_write);
        setting = getView().findViewById(R.id.id_mine_setting);

        wallet = getView().findViewById(R.id.id_mine_wallet);
        vip = getView().findViewById(R.id.id_mine_vip);
        mycollection = getView().findViewById(R.id.id_mine_mycollection);
        mypunish = getView().findViewById(R.id.id_mine_mypunish);
        device = getView().findViewById(R.id.id_mine_device);
        update = getView().findViewById(R.id.id_mine_update);

        isClick = false;
    }

    private void initEvent() {
        write.setOnClickListener(this);
        setting.setOnClickListener(this);
        wallet.setOnClickListener(this);
        vip.setOnClickListener(this);
        mypunish.setOnClickListener(this);
        mycollection.setOnClickListener(this);
        device.setOnClickListener(this);
        update.setOnClickListener(this);
        avatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!isClick){
            switch (v.getId()){
                case R.id.id_mine_write:
                    isClick = true;
                    ScaleAnimation scaleAnimation = new ScaleAnimation((float) 1.0,(float)0.95,(float)1.0,(float)0.95);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillBefore(true);
                    v.startAnimation(scaleAnimation);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getContext(), ComplieActivity.class));
                        }
                    },300);

                    break;
                case R.id.id_mine_avatar:
                    Intent intent = new Intent(getContext(), UserActivity.class);
                    intent.putExtra("type","mine");
                    intent.putExtra("account",user.getUsername());
                    startActivity(intent);
                    break;
                case R.id.id_mine_setting:
                    startActivity(new Intent(getContext(), SettingActivity.class));
                    break;
                case R.id.id_mine_wallet:

                    break;
                case R.id.id_mine_vip:

                    break;
                case R.id.id_mine_mypunish:

                    break;
                case R.id.id_mine_mycollection:

                    break;
                case R.id.id_mine_device:
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
                    break;
                case R.id.id_mine_update:
                    startActivity(new Intent(getContext(), VersionActivity.class));
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}
