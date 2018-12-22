package com.android.ql.lf.baselibaray.ui.activity;

import android.support.annotation.CallSuper;

import com.android.ql.lf.baselibaray.application.BaseApplication;
import com.android.ql.lf.baselibaray.component.ApiServerModule;
import com.android.ql.lf.baselibaray.component.DaggerApiServerComponent;
import com.android.ql.lf.baselibaray.present.GetDataFromNetPresent;

import javax.inject.Inject;

public abstract class BaseSplashActivity extends BaseActivity {

    @Inject
    GetDataFromNetPresent getDataFromNetPresent;

    @CallSuper
    @Override
    public void initView() {
        DaggerApiServerComponent.builder().appComponent(BaseApplication.getInstance().getAppComponent()).apiServerModule(new ApiServerModule()).build().inject(this);
    }

    public GetDataFromNetPresent getGetDataFromNetPresent() {
        return getDataFromNetPresent;
    }

}
