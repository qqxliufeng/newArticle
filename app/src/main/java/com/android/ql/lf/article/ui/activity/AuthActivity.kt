package com.android.ql.lf.article.ui.activity

import android.content.Intent
import android.os.Build
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.fragments.mine.AccountSafeFragment
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.auth.WbConnectErrorMessage
import com.sina.weibo.sdk.auth.sso.SsoHandler
import kotlinx.android.synthetic.main.activity_auth_layout.*
import org.jetbrains.anko.toast

/**
 * Created by lf on 18.11.21.
 * @author lf on 18.11.21
 */
class AuthActivity : BaseActivity() {

    private var mSsoHandler: SsoHandler? = null

    override fun getLayoutId() = R.layout.activity_auth_layout

    private val accountSafeFragment by lazy {
        AccountSafeFragment()
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setSupportActionBar(mTlActivityAuth)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mTlActivityAuth.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
        mTlActivityAuth.setNavigationOnClickListener { finish() }
        supportFragmentManager.beginTransaction().replace(R.id.mFlAuthContainer, accountSafeFragment).commit()
    }

    fun weiboAuth() {
        mSsoHandler = SsoHandler(this)
        mSsoHandler?.authorize(object : WbAuthListener {
            override fun onSuccess(p0: Oauth2AccessToken?) {
                accountSafeFragment.onWeiBoAuthSuccess(p0!!)
                toast("绑定成功")
            }

            override fun onFailure(p0: WbConnectErrorMessage?) {
                toast("绑定失败")
            }

            override fun cancel() {
                toast("绑定取消")
            }
        })
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity.onActivityResult}
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null) {
            mSsoHandler?.authorizeCallBack(requestCode, resultCode, data)
        }
    }
}