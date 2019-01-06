package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinhu.oneschool.R;

public class Anim {
    public static int ROTATE_UP =  R.anim.rotate_up;
    public static int ROTATE_DOWN =  R.anim.rotate_down;
    public static int TRANSLATE_UP =  R.anim.larger_y;
    public static int TRANSLATE_DOWN =  R.anim.smaller_y;

    public static void start(Context context, long time,int anim, ImageView img){
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        animation.setFillAfter(true);
        animation.setDuration(time);
        img.startAnimation(animation);
    }


    public static void start(Context context, long time,int anim, LinearLayout linearLayout){
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        animation.setFillAfter(true);
        animation.setDuration(time);
        linearLayout.startAnimation(animation);
    }

}
