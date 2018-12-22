package com.android.ql.lf.baselibaray.component;


import com.android.ql.lf.baselibaray.present.GetDataFromNetPresent;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/10/16 0016.
 */

@Module
public class ApiServerModule {

    @ApiServerScope
    @Provides
    public ApiServer createApiServer(Retrofit retrofit) {
        return retrofit.create(ApiServer.class);
    }

    @ApiServerScope
    @Provides
    public GetDataFromNetPresent createNetPresent(ApiServer apiServer) {
        return new GetDataFromNetPresent(apiServer);
    }

}
