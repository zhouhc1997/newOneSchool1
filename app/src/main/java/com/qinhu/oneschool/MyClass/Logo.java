package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.qinhu.oneschool.R;

public class Logo extends View  {
    public Logo(Context context) {
        super(context);
    }

    public Logo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Logo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Logo(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {


        Paint paint1 = new Paint();
        paint1.setColor(getResources().getColor(R.color.logoColor));
        paint1.setAntiAlias(true);

        Paint paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setAntiAlias(true);

        Paint paint3 = new Paint();
        paint3.setColor(getResources().getColor(R.color.black));
        paint3.setAntiAlias(true);

        Paint paint4 = new Paint();
        paint4.setColor(getResources().getColor(R.color.logoDark));
        paint4.setAntiAlias(true);

        canvas.drawRoundRect(new RectF(90 , 5 ,110 ,95),10,10,paint3);
        canvas.drawOval(0,0,100,100,paint1);
        canvas.drawOval(20,20,80,80,paint2);

        canvas.drawRoundRect(new RectF(115 , 5 ,170 ,25),10,10,paint3);
        canvas.drawRoundRect(new RectF(150 , 5 ,170 ,95),10,10,paint3);

        canvas.drawRoundRect(new RectF(175 , 5 ,215 ,25),10,10,paint4);
        canvas.drawRoundRect(new RectF(175 , 40 ,215 ,60),10,10,paint4);
        canvas.drawRoundRect(new RectF(175 , 75 ,215 ,95),10,10,paint4);

        super.onDraw(canvas);
   }







}
