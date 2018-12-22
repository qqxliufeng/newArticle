package com.android.ql.lf.baselibaray.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ql.lf.baselibaray.R;
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity;
import com.android.ql.lf.baselibaray.utils.BaseConfig;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.Set;

import rx.Subscription;


/**
 * @author Administrator
 * @date 2017/10/17 0017
 */

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    protected Subscription subscription = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() == 0) {
            throw new IllegalStateException("请先设置布局文件");
        }
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected ScreenSize getScreenSize() {
        ScreenSize screenSize = new ScreenSize();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenSize.width = displayMetrics.widthPixels;
        screenSize.height = displayMetrics.heightPixels;
        return screenSize;
    }

    protected int getStatusBarHeight() {
        return ((BaseActivity) mContext).getStatusHeight();
    }

    protected int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        int[] attribute = new int[]{android.R.attr.actionBarSize};
        TypedArray array = mContext.obtainStyledAttributes(tv.resourceId, attribute);
        int actionBarHeight1 = array.getDimensionPixelSize(0 /* index */, -1 /* default size */);
        array.recycle();
        return actionBarHeight1;
    }

    protected void openImageChoose(Set<MimeType> mimeTypes, int maxSelectable) {
        Matisse.from(this)
                .choose(mimeTypes)
                .imageEngine(new GlideEngine())
                .maxSelectable(maxSelectable)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, BaseConfig.getFileProvidePath(mContext)))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .theme(R.style.my_matisse_style)
                .forResult(0);
    }

    public void finish() {
        ((Activity) mContext).finish();
    }

    public void onMyActivityResult(int requestCode, int resultCode, Intent data){}

    @Override
    public void onDestroyView() {
        unsubscribe(subscription);
        super.onDestroyView();
    }

    public void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    public static class ScreenSize {
        public int width;
        public int height;
    }

}
