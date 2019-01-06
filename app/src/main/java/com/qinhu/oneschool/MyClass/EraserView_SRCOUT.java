package com.qinhu.oneschool.MyClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class EraserView_SRCOUT extends ImageView {
    private Paint mBitPaint;
    private Bitmap BmpDST,BmpSRC;
    private Path mPath;
    private float mPreX,mPreY;
    private Boolean isPaint = false;

    public EraserView_SRCOUT(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        mBitPaint = new Paint();
        mBitPaint.setColor(Color.RED);
        mBitPaint.setStyle(Paint.Style.STROKE);
        mBitPaint.setStrokeWidth(20);
        mPath = new Path();
    }

    public void setDraw(int draw){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        BmpSRC = BitmapFactory.decodeResource(getResources(), draw,options);
        BmpDST = Bitmap.createBitmap(BmpSRC.getWidth(),BmpSRC.getHeight(),Bitmap.Config.ARGB_8888);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mPath.moveTo(event.getX(),event.getY());
                    mPreX = event.getX();
                    mPreY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float endX = (mPreX+event.getX())/2;
                    float endY = (mPreY+event.getY())/2;
                    mPath.quadTo(mPreX,mPreY,endX,endY);
                    mPreX = event.getX();
                    mPreY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            postInvalidate();
            return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);
        Canvas c = new Canvas(BmpDST);
        c.drawPath(mPath,mBitPaint);
        canvas.drawBitmap(BmpSRC,0,0,mBitPaint);
        mBitPaint.setXfermode(null);
        canvas.drawBitmap(BmpDST,0,0,mBitPaint);
        mBitPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.restoreToCount(layerId);
    }

    private  Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        //store
        cv.restore();//存储
        return newbmp;
    }


    public Bitmap getBmpDST() {
        return  toConformBitmap(BmpSRC,BmpDST);
    }

    public void setIsPaint(Boolean b){
        this.isPaint = b;
    }

}
