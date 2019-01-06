package com.qinhu.oneschool.MyClass;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;

import com.qinhu.oneschool.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignKeyWordTextView extends AppCompatTextView {

    //原始文本
    private String mOriginalText = "";
    //关键字
    private String mSignText;
    //关键字颜色
    private int mSignTextColor;

    public SignKeyWordTextView(Context context) {
        super(context);
    }

    public SignKeyWordTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttrs(attrs);
    }

    //初始化自定义属性
    private void initializeAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SignKeyWordTextView);
        //获取关键字
        mSignText = typedArray.getString(R.styleable.SignKeyWordTextView_signText);
        //获取关键字颜色
        mSignTextColor = typedArray.getColor(R.styleable.SignKeyWordTextView_signTextColor, getTextColors().getDefaultColor());
        typedArray.recycle();
    }

    //重写setText方法
    @Override
    public void setText(CharSequence text, BufferType type) {
        this.mOriginalText = text.toString();
        super.setText(matcherSignText(), type);
    }

    /**
     * 匹配关键字，并返回SpannableStringBuilder对象
     * @return
     */
    private SpannableStringBuilder matcherSignText() {
        if (TextUtils.isEmpty(mOriginalText)) {
            return new SpannableStringBuilder("");
        }
        if (TextUtils.isEmpty(mSignText)) {
            return new SpannableStringBuilder(mOriginalText);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(mOriginalText);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mSignTextColor);
        int size = mSignText.length();
        int[] number = new int[size+1];
        for (int index = 0; index < size; index++) {
            char c = mSignText.charAt(index);
            Pattern p = Pattern.compile(String.valueOf(c));
            Matcher m = p.matcher(mOriginalText);
            while (m.find()) {
                number[index] = m.start();
                builder.setSpan(foregroundColorSpan, number[0], number[0] + size, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    /**
     * 设置关键字
     * @param signText 关键字
     */
    public void setSignText(String signText) {
        mSignText = signText;
        setText(mOriginalText);
    }

    /**
     * 设置关键字颜色
     * @param signTextColor 关键字颜色
     */
    public void setSignTextColor(int signTextColor) {
        mSignTextColor = signTextColor;
        setText(mOriginalText);
    }

}
