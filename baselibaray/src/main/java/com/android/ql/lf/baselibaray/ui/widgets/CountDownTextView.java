package com.android.ql.lf.baselibaray.ui.widgets;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by lf on 18.11.8.
 *
 * @author lf on 18.11.8
 */
public class CountDownTextView extends AppCompatTextView {

    private long mMillisInFuture = 0L;
    private long countDownInterval = 0L;

    private CountDownTimer mCountDownTimer;

    private OnCountDownTimerListener onCountDownTimerListener;

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(OnCountDownTimerListener onCountDownTimerListener) {
        init(1000 * 60, 1000, onCountDownTimerListener);
    }

    public void init(long mMillisInFuture, long countDownInterval, OnCountDownTimerListener onCountDownTimerListener) {
        this.mMillisInFuture = mMillisInFuture;
        this.countDownInterval = countDownInterval;
        this.onCountDownTimerListener = onCountDownTimerListener;
        mCountDownTimer = new CountDownTimer(mMillisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                setEnabled(false);
                setText(millisUntilFinished / 1000 + " 秒");
                if (CountDownTextView.this.onCountDownTimerListener != null) {
                    CountDownTextView.this.onCountDownTimerListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                setEnabled(true);
                setText("重新发送？");
                if (CountDownTextView.this.onCountDownTimerListener != null) {
                    CountDownTextView.this.onCountDownTimerListener.onFinish();
                }
            }
        };
    }

    public void setOnCountDownTimerListener(OnCountDownTimerListener onCountDownTimerListener) {
        this.onCountDownTimerListener = onCountDownTimerListener;
    }

    public void start() {
        if (mMillisInFuture == 0L || mCountDownTimer == null) {
            init(1000 * 60, 1000,null);
        }
        mCountDownTimer.start();
    }

    public void cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public long getCountDownInterval() {
        return countDownInterval;
    }

    public long getMillisInFuture() {
        return mMillisInFuture;
    }

    public interface OnCountDownTimerListener {

        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
