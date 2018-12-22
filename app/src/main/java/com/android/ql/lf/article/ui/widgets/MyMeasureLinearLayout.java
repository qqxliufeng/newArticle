package com.android.ql.lf.article.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class MyMeasureLinearLayout extends LinearLayout {
    public static final String TAG = "MeasureLinearLayout";
    public static final boolean DEBUG = true;

    private FragmentSizeObserver mObserver;

    private int initBottom = -1;
    private int initLeft  = -1;

    public MyMeasureLinearLayout(Context context){
        this(context, null);
    }

    public MyMeasureLinearLayout(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public MyMeasureLinearLayout(Context context, AttributeSet attrs,
            int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, " changed  "+changed +" b "+b);
        if(initBottom == -1 || initLeft == -1){
            initBottom = b;
            initLeft   = l;
            return;
        }
        if(changed){
            Log.d(TAG, " height "+b +" width "+r
                    +" initBottom "+initBottom+" initLeft "+initLeft);
            int height = b - initBottom; //高度变化值（弹出输入法，布局变小，则为负值）
            int width  = r - initLeft;  // 当前屏幕宽度（对应输入法而言无影响）
            onInputSizeChanged(width, height);
        }   
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    private void onInputSizeChanged(int widthDiff,int heightDiff) {
        if(mObserver != null){
            mObserver.onFragmentSizeChanged(widthDiff, heightDiff);
        }
    }
    public void registFragmentSizeObserver(FragmentSizeObserver observer) {
        mObserver = observer;       
    }
    public interface FragmentSizeObserver{
        public void onFragmentSizeChanged(int wdiff,int hdiff);
    }
}