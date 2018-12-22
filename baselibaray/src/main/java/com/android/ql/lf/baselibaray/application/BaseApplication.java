package com.android.ql.lf.baselibaray.application;

import android.support.annotation.CallSuper;
import android.support.multidex.MultiDexApplication;

import com.android.ql.lf.baselibaray.component.AppComponent;
import com.android.ql.lf.baselibaray.component.AppModule;
import com.android.ql.lf.baselibaray.component.DaggerAppComponent;

public class BaseApplication extends MultiDexApplication {

    protected AppComponent appComponent;

    protected static BaseApplication instance;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDagger();
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }
}
