package com.android.ql.lf.baselibaray.component;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/20 0020.
 */

public class ApiParams extends HashMap<String, Object> {

    public static final String MOD_NAME = "mod";
    public static final String ACT_NAME = "act";


    public ApiParams addParam(String paramName, Object paramValue) {
        put(paramName, paramValue);
        return this;
    }
}
