package com.android.ql.lf.article.application

import cn.jpush.android.api.JPushInterface
import com.android.ql.lf.baselibaray.application.BaseApplication
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.auth.AuthInfo
import com.tencent.tauth.Tencent

class MyApplication : BaseApplication() {

    companion object {

        private lateinit var instance: MyApplication

        fun getInstance() = instance
    }

    var tencent:Tencent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        WbSdk.install(this, AuthInfo(this,
            BaseConfig.WB_APP_ID,
            BaseConfig.WB_REDIRECT_URL,
            BaseConfig.WB_SCOPE)
        )
        tencent = Tencent.createInstance(BaseConfig.TENCENT_ID, this)
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }
}