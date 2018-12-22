package com.android.ql.lf.baselibaray.ui.fragment;

import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.ql.lf.baselibaray.R;
import com.android.ql.lf.baselibaray.ui.widgets.NoScrollViewPager;

public abstract class BaseViewPagerFragment extends BaseFragment {

    protected NoScrollViewPager noScrollViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_view_pager_layout;
    }

    @CallSuper
    @Override
    protected void initView(View view) {
        noScrollViewPager = view.findViewById(R.id.mVpBaseContainer);
        if (isSetMarginTop()) {
            ((ViewGroup.MarginLayoutParams) noScrollViewPager.getLayoutParams()).topMargin = getStatusBarHeight();
        }
        noScrollViewPager.setAdapter(getViewPagerAdapter());
        noScrollViewPager.setOffscreenPageLimit(getOffscreenPageLimit());
    }


    public boolean isSetMarginTop() {
        return true;
    }

    public int getOffscreenPageLimit() {
        if (noScrollViewPager.getAdapter() != null) {
            return noScrollViewPager.getAdapter().getCount();
        } else {
            return 0;
        }
    }

    public abstract FragmentPagerAdapter getViewPagerAdapter();

    public abstract void positionFailed(int position);

    public void positionFragment(int position) {
        if (noScrollViewPager.getAdapter() != null) {
            if (position < 0 || position > noScrollViewPager.getAdapter().getCount()) {
                positionFailed(position);
            } else {
                noScrollViewPager.setCurrentItem(position);
            }
        } else {
            throw new NullPointerException("base viewpager adapter is null, please set the adapter first");
        }
    }
}
