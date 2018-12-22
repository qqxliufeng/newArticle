package com.android.ql.lf.baselibaray.component;



import android.app.Service;

import com.android.ql.lf.baselibaray.ui.activity.BaseSplashActivity;
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity;
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment;

import dagger.Component;

/**
 * @author Administrator
 * @date 2017/10/16 0016
 */

@ApiServerScope
@Component(modules = {ApiServerModule.class}, dependencies = AppComponent.class)
public interface ApiServerComponent {

    void inject(FragmentContainerActivity activity);

    void inject(BaseNetWorkingFragment baseNetWorkingFragment);

    void inject(BaseSplashActivity splashActivity);
//
//    void inject(SplashActivity splashActivity);
//
//    void inject(SelectAddressActivity selectAddressActivity);

}
