package com.android.ql.lf.baselibaray.utils;

import android.content.Context;
import android.os.Environment;

public class BaseConfig {

    public static final String BASE_IP = "http://article.581vv.com/";

    public static final String md5Token = "0e468854ec9859feb51f7a08d51db106";
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String APP_PATH = BASE_PATH + "/qlarticle";
    public static final String IMAGE_PATH = APP_PATH + "/img/";
    public static final String WX_APP_ID = "wxabd0f5c0faf7a1a1";

    public static final String TENCENT_ID = "1107995446";

    public static final String WB_APP_ID = "3743717577";

    public static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String WB_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static String getFileProvidePath(Context context) {
        return context.getPackageName() + ".fileProvider";
    }

}
