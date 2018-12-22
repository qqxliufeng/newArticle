package com.android.ql.lf.article.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by lf on 18.11.28.
 *
 * @author lf on 18.11.28
 */
public class MyTestJushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if ("test".equals(message)){
            throw new NullPointerException("test");
        }
    }
}
