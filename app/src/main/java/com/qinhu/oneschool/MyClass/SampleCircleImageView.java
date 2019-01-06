package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.qinhu.oneschool.R;

public class SampleCircleImageView extends View {
    /**
     * 默认圆角大小
     */
    private static final int DEFUALT_RADIUS = 20;
    /**
     * 源图片
     */
    private Bitmap mSrc;
    /**
     * 圆角大小，默认为20
     */
    private int mRadius = DEFUALT_RADIUS;
    /**
     * 控件的宽度
     */
    private int mWidth;
    /**
     * 控件的高度
     */
    private int mHeight;
    private Context mContext;

    private int a = 255;


    public SampleCircleImageView(Context context) {
        super(context);
        init(context ,null ,0);
    }

    public SampleCircleImageView(Context context ,Bitmap bitmap) {
        super(context);
        Log.d("danxx" ,"create SampleCircleImageView");
        this.mSrc = bitmap;
        init(context ,null ,0);
    }

    public SampleCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context ,attrs ,0);
    }

    public SampleCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context ,attrs ,defStyleAttr);
    }

    private void init(Context context ,AttributeSet attrs ,int defStyleAttr){
        mContext = context;
        if(attrs != null){
            /**Load the styled attributes and set their properties**/
            TypedArray typedArray = context.obtainStyledAttributes(attrs , R.styleable.SampleCircleImageView ,defStyleAttr ,0);
            mSrc = BitmapFactory.decodeResource(context.getResources() ,typedArray.getResourceId(R.styleable.SampleCircleImageView_src ,0));
            mRadius = (int) typedArray.getDimension(R.styleable.SampleCircleImageView_radius ,dp2px(DEFUALT_RADIUS));
            typedArray.recycle();
        }

    }

    /**
     * 测量控件大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        /**获取宽高的尺寸**/
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 测量宽度
         */
        if(widthSpecMode == MeasureSpec.EXACTLY){  //宽为具体值或者是填满父控件就直接赋值 match_parent , accurate
            mWidth = widthSpecSize;
        }else{
            /**图片显示时原始大小**/
            int srcWidth = mSrc.getWidth() + getPaddingLeft() + getPaddingRight();
            if(widthSpecMode == MeasureSpec.AT_MOST){ //wrap_content,子控件不能超过父控件,此时我们取传递过来的大小和图片本身大小的小者
                mWidth = Math.min(widthSpecSize , srcWidth);
            }else{
                //没有要求，可以随便大小
                mWidth = srcWidth;
            }
        }

        /**
         * 测量高度，逻辑跟测量宽度是一样的
         */
        if(heightSpecMode == MeasureSpec.EXACTLY){  //match_parent , accurate
            mHeight = heightSpecSize;
        }else{
            /**图片显示时原始大小**/
            int srcHeigth = mSrc.getHeight() + getPaddingTop() + getPaddingBottom();
            if(heightSpecMode == MeasureSpec.AT_MOST){ //wrap_content
                mHeight = Math.min(heightSpecSize , srcHeigth);
            }else{
                //没有要求，可以随便大小
                mHeight = srcHeigth;
            }
        }
        setMeasuredDimension(mWidth ,mHeight);
    }

    /**
     * 绘制控件
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(createRoundConerImage(mSrc) ,0 ,0 ,null);
    }

    public void setRadius(int radius){
        this.mRadius = radius;
    }


    public void setSrc(Bitmap bitmap){
        this.mSrc = bitmap;
    }

    private Bitmap createRoundConerImage(Bitmap source){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target  = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0 , 0 ,mWidth ,mHeight);
        source = Bitmap.createScaledBitmap(source,mWidth,mHeight, false);
        canvas.drawRoundRect(rect ,mRadius ,mRadius ,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source ,0 ,0 ,paint);
        return target;
    }


//    private Bitmap createShape(){
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(mShadowColor);
//        paint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
//        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(target);
//        RectF rectF = new RectF(mShadowRadius, mShadowRadius, width - mShadowRadius, height - mShadowRadius);
//        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
//
//        return
//    }


    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
