package com.android.ql.lf.baselibaray.ui.fragment;

import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.android.ql.lf.baselibaray.R;
import com.android.ql.lf.baselibaray.data.BaseNetResult;
import com.android.ql.lf.baselibaray.data.ListParseHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by Administrator on 2017/10/17 0017.
 *
 * @author lf
 */

public abstract class BaseRecyclerViewFragment<T> extends BaseNetWorkingFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public SwipeRefreshLayout mSwipeRefreshLayout;

    public RecyclerView mRecyclerView;

    protected BaseQuickAdapter<T, BaseViewHolder> mBaseAdapter;
    protected ArrayList<T> mArrayList = new ArrayList<>();
    protected RecyclerView.LayoutManager mManager;

    protected int currentPage = 0;

    protected boolean isNeedLoad = true;
    protected boolean isFirstRefresh = true;
    private boolean isTest = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_recycler_view_layout;
    }


    //设置空View相关的方法    start
    protected int getEmptyLayoutId() {
        return R.layout.layout_list_empty_layout;
    }

    protected void setEmptyMessage(String emptyMessage) {
        TextView tv_empty = getEmptyTextView();
        tv_empty.setText(emptyMessage);
    }

    protected TextView getEmptyTextView() {
        return mBaseAdapter.getEmptyView().findViewById(R.id.mTvRecyclerViewEmpty);
    }

    protected String getEmptyMessage() {
        return "暂无数据";
    }

    protected void setEmptyView() {
        mBaseAdapter.setEmptyView(getEmptyLayoutId());
        setEmptyMessage(getEmptyMessage());
        mBaseAdapter.notifyDataSetChanged();
    }

    protected void setEmptyViewNoLoginStatus() {
        onRequestEnd(-1);
        setRefreshEnable(false);
        setEmptyView();
        TextView emptyTextView = getEmptyTextView();
        emptyTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        emptyTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, getResources().getDisplayMetrics());
        emptyTextView.setPadding(paddingLeft, paddingLeft / 2, paddingLeft, paddingLeft / 2);
        emptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyClick();
            }
        });
    }
    //设置空View相关的方法    end

    @Override
    @CallSuper
    protected void initView(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.id_srl_base_recycler_view);
        mRecyclerView = view.findViewById(R.id.id_rv_base_recycler_view);
        mBaseAdapter = createAdapter();
        if (mBaseAdapter == null) {
            return;
        }
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext,
                android.R.color.holo_red_dark),
                ContextCompat.getColor(mContext, android.R.color.holo_blue_dark),
                ContextCompat.getColor(mContext, android.R.color.holo_green_dark)
        );
        mBaseAdapter.openLoadAnimation();
        if (isNeedLoad) {
            mBaseAdapter.setOnLoadMoreListener(this, mRecyclerView);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(mBaseAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(getItemDecoration());
        if (isFirstRefresh) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                }
            });
        }
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    onMyItemClick(adapter, view, position);
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    onMyItemChildClick(adapter, view, position);
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    onMyItemLongClick(adapter, view, position);
                }
            }
        });
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mManager = linearLayoutManager;
        return linearLayoutManager;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.recycler_view_divider));
        return decoration;
    }

    public void setDividerDecoration() {
        DividerItemDecoration decor = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        decor.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.recycler_view_divider));
        mRecyclerView.addItemDecoration(decor);
    }

    public void onMyItemClick(BaseQuickAdapter adapter, View view, int position) {
    }

    public void onMyItemChildClick(BaseQuickAdapter adapter, View view, int position) {
    }

    public void onMyItemLongClick(BaseQuickAdapter adapter, View view, int position) {
    }

    protected abstract BaseQuickAdapter<T, BaseViewHolder> createAdapter();

    public void setRefreshEnable(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    public void setLoadEnable(final boolean enable) {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseAdapter.setEnableLoadMore(enable);
            }
        }, 100);
    }

    @Override
    public void onLoadMoreRequested() {
        onLoadMore();
    }

    @CallSuper
    protected void onLoadMore() {
        currentPage++;
    }

    @Override
    public void onRequestEnd(int requestID) {
        super.onRequestEnd(requestID);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRequestFail(int requestID, @NotNull Throwable e) {
        super.onRequestFail(requestID, e);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        setEmptyView();
    }

    //    @CallSuper
    @Override
    public void onRefresh() {
        currentPage = 0;
        mArrayList.clear();
        mBaseAdapter.setNewData(mArrayList);
    }

    public void testAdd(T t) {
        for (int i = 0; i < 10; i++) {
            mArrayList.add(t);
        }
        mBaseAdapter.setNewData(mArrayList);
        onRequestEnd(-1);
        mBaseAdapter.loadMoreEnd();
    }

    public void onPostRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void processList(String result, Class<T> clazz) {
        try {
            BaseNetResult baseNetResult = checkResultCode(result);
            if (baseNetResult != null && Objects.equals(baseNetResult.code, SUCCESS_CODE)) {
                processList(((JSONObject) baseNetResult.obj), clazz);
            } else {
                if (currentPage == 0) {
                    setEmptyView();
                } else {
                    mBaseAdapter.loadMoreEnd();
                    mBaseAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            setEmptyView();
        }
    }

    public void processList(JSONObject json, Class<T> clazz) {
        try {
            if (json != null) {
                ArrayList<T> tempList = new ListParseHelper<T>().fromJson(json.toString(), clazz);
                if (tempList != null && !tempList.isEmpty()) {
                    actionTempList(tempList);
                    mArrayList.addAll(tempList);
                    mBaseAdapter.loadMoreComplete();
                    mBaseAdapter.disableLoadMoreIfNotFullPage();
                } else {
                    if (currentPage == 0) {
                        mBaseAdapter.setEmptyView(getEmptyLayoutId());
                        setEmptyMessage(getEmptyMessage());
                    } else {
                        mBaseAdapter.loadMoreEnd();
                    }
                }
                mBaseAdapter.notifyDataSetChanged();
            } else {
                if (currentPage == 0) {
                    setEmptyView();
                } else {
                    mBaseAdapter.loadMoreEnd();
                    mBaseAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            setEmptyView();
        }
    }

    public void onEmptyClick() {
    }

    public void actionTempList(ArrayList<T> tempList){}

}
