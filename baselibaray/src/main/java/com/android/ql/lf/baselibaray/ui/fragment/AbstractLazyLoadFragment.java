package com.android.ql.lf.baselibaray.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by lf on 2017/11/30 0030.
 *
 * @author lf on 2017/11/30 0030
 */

public abstract class AbstractLazyLoadFragment<T> extends BaseRecyclerViewFragment<T> {

    protected boolean isVisible = false;
    protected boolean isPrepared = false;
    protected boolean isLoad = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    protected void onVisible() {
        if (isLoad) {
            return;
        }
        lazyLoad();
    }

    @Override
    protected void initView(View view) {
        isFirstRefresh = false;
        super.initView(view);
    }

    @Override
    public void onRefresh() {
        if (!isVisible || !isPrepared) {
            return;
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

            }
        });
        super.onRefresh();
        loadData();
    }


    public void onLoginRefresh(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

            }
        });
        super.onRefresh();
        loadData();
    }

    public void onLogoutRefresh(){
        onLoginRefresh();
    }

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 懒加载方法
     */
    protected void lazyLoad() {
        onRefresh();
    }

    protected void onInvisible() {
    }
}
