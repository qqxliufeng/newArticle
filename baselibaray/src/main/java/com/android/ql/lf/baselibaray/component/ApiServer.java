package com.android.ql.lf.baselibaray.component;



import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2017/10/16 0016.
 *
 * @author lf
 */

public interface ApiServer {

    String BASE_IP_POSTFIX = "api/{postfix1}/{postfix2}";

    /**
     * 以post的请求方式，请求网络数据
     *
     * @param params 请求参数
     * @return 请求结果
     */
    @FormUrlEncoded
    @POST(BASE_IP_POSTFIX)
    Observable<String> getDataByPost(@Path("postfix1") String postfix1, @Path("postfix2") String postfix2, @FieldMap ApiParams params, @Field("token") String token);

    /**
     * 以post的请求方式，请求网络数据
     * @return 请求结果
     */
    @FormUrlEncoded
    @POST(BASE_IP_POSTFIX)
    Observable<String> getDataByPost(@Path("postfix1") String postfix1, @Path("postfix2") String postfix2, @Field("token") String token);

    /**
     * 以get的请求方式，请求网络数据
     *
     * @return 请求结果
     */
    @GET(BASE_IP_POSTFIX)
    Observable<String> getDataByGet(@Path("postfix1") String postfix1, @Path("postfix2") String postfix2, @QueryMap ApiParams params);

    /**
     * 以get的请求方式，请求网络数据
     *
     * @param map 请求参数
     * @return 请求结果
     */
    @GET(BASE_IP_POSTFIX)
    Observable<String> getDataByGet(@QueryMap HashMap<String, String> map, @Query("token") String token);

    /**
     * 上传图片
     *
     * @param postfix1
     * @param postfix2
     * @param partList
     * @return
     */
    @Multipart
    @POST(BASE_IP_POSTFIX)
    Observable<String> uploadFiles(
            @Path("postfix1") String postfix1,
            @Path("postfix2") String postfix2,
            @Part List<MultipartBody.Part> partList);
}
