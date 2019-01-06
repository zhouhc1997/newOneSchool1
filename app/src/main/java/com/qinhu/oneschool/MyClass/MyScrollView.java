package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private OnScollChangedListener onScollChangedListener = null;
    private boolean canScroll;
    private GestureDetector mGestureDetector;
    OnTouchListener mGestureListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setOnScollChangedListener(OnScollChangedListener onScollChangedListener) {
        this.onScollChangedListener = onScollChangedListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScollChangedListener != null) {
            onScollChangedListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface OnScollChangedListener {
        void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}
