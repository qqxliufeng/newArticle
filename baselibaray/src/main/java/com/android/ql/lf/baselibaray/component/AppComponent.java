package com.android.ql.lf.baselibaray.component;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 *
 * @author Administrator
 * @date 2017/10/16 0016
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Retrofit retrofit();

}
