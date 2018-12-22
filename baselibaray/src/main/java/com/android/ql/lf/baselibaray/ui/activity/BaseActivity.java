package com.android.ql.lf.baselibaray.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.android.ql.lf.baselibaray.R;
import com.android.ql.lf.baselibaray.interfaces.INetDataPresenter;

import org.jetbrains.annotations.NotNull;

public abstract class BaseActivity extends AppCompatActivity implements INetDataPresenter {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
    }

    public int getStatusBarColor() {
        return ContextCompat.getColor(this, R.color.colorPrimary);
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getStatusBarColor());
        }
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }


    public int getStatusHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    /**
     * 布局文件
     *
     * @return 资源ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化控件
     */
    public abstract void initView();

    @Override
    public void onRequestStart(int requestID) {
    }

    @Override
    public void onRequestFail(int requestID, @NotNull Throwable e) {
    }

    @Override
    public <T> void onRequestSuccess(int requestID, T result) {
    }

    @Override
    public void onRequestEnd(int requestID) {
    }
}
