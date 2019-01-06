package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.qinhu.oneschool.R;

public class CornerImageView extends View {
    private int width = 400;
    private int height = 400;
    private Bitmap dstBmp = null;
    private Bitmap srcBmp = null;
    private Paint mPaint;

    public CornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        dstBmp = makeDst(width,height);
        srcBmp = makeSrc(width,height);
        mPaint = new Paint();
    }

    private Bitmap makeSrc(int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xffffcc44);
        c.drawOval(new RectF(100,100,140,140),p);
        return bm;
    }

    private Bitmap makeDst(int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas c1 = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xffff0000);

        c1.drawRect(100,100,400,200,p);

        return bm;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dstBmp == null)
            return;

        int layerId = canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(dstBmp,0,0,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBmp,0,0,mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }



}
