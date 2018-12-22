package com.android.ql.lf.article.ui.fragments.mine

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.application.MyApplication
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.ui.activity.AuthActivity
import com.android.ql.lf.article.utils.ACCOUNT_SAFE_ACT
import com.android.ql.lf.article.utils.MEMBER_MODULE
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.android.ql.lf.baselibaray.utils.RxBus
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.fragment_account_safe_layout.*
import org.jetbrains.anko.support.v4.toast


/**
 * Created by lf on 18.11.21.
 * @author lf on 18.11.21
 */
class AccountSafeFragment : BaseNetWorkingFragment(), IUiListener {

    override fun getLayoutId() = R.layout.fragment_account_safe_layout

    private var type: Int = 1

    private val wxAuthSubscription by lazy {
        RxBus.getDefault().toObservable(BaseResp::class.java).subscribe {
            onWeiXinAuthSuccess((it as SendAuth.Resp).code)
        }
    }

    private val iwxapi by lazy {
        WXAPIFactory.createWXAPI(mContext, BaseConfig.WX_APP_ID, true)
    }

    override fun onResume() {
        super.onResume()
        mTvAccountSafePhone.text = UserInfo.user_phone
    }

    override fun initView(view: View?) {
        wxAuthSubscription
        mRlAccountSafeResetPhone.setOnClickListener {
            FragmentContainerActivity.from(mContext).setClazz(ResetPhoneFragment::class.java).setTitle("修改手机号").setNeedNetWorking(true).start()
        }
        if (UserInfo.user_pass == null || UserInfo.user_pass == "null") {
            mTvAccountSafeResetPassword.visibility = View.GONE
        } else {
            mTvAccountSafeResetPassword.visibility = View.VISIBLE
            mTvAccountSafeResetPassword.setOnClickListener {
                FragmentContainerActivity.from(mContext).setClazz(ResetPasswordFragment::class.java).setTitle("重置密码")
                    .setNeedNetWorking(true).start()
            }
        }
        if (TextUtils.isEmpty(UserInfo.user_wx)) {
            mIvAccountSafeWX.setImageResource(R.drawable.img_wx_unselect_icon)
            mTvAccountSafeWX.text = "未绑定"
            mLlAccountSafeWX.setOnClickListener {
                iwxapi.registerApp(BaseConfig.WX_APP_ID)
                val req = SendAuth.Req()
                req.scope = "snsapi_userinfo"
                req.state = "wechat_sdk_ql_bs"
                iwxapi.sendReq(req)
            }
        } else {
            mIvAccountSafeWX.setImageResource(R.drawable.img_wx_select_icon)
            mTvAccountSafeWX.text = "已绑定"
        }
        if (TextUtils.isEmpty(UserInfo.user_qq)) {
            mIvAccountSafeQQ.setImageResource(R.drawable.img_qq_unselect_icon)
            mTvAccountSafeQQ.text = "未绑定"
            mLlAccountSafeQQ.setOnClickListener {
                if (!MyApplication.getInstance().tencent!!.isSessionValid) {
                    MyApplication.getInstance().tencent?.login(
                        this@AccountSafeFragment,
                        "all",
                        this@AccountSafeFragment
                    )
                }
            }
        } else {
            mIvAccountSafeQQ.setImageResource(R.drawable.img_qq_select_icon)
            mTvAccountSafeQQ.text = "已绑定"
        }
        if (TextUtils.isEmpty(UserInfo.user_weibo)) {
            mIvAccountSafeWB.setImageResource(R.drawable.img_wb_unselect_icon)
            mTvAccountSafeWB.text = "未绑定"
            mLlAccountSafeWB.setOnClickListener {
                (mContext as AuthActivity).weiboAuth()
            }
        } else {
            mIvAccountSafeWB.setImageResource(R.drawable.img_wb_select_icon)
            mTvAccountSafeWB.text = "已绑定"
        }
    }

    fun onWeiBoAuthSuccess(token: Oauth2AccessToken) {
        type = 3
        mPresent.getDataByPost(
            0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, ACCOUNT_SAFE_ACT)
                .addParam("type", "3")
                .addParam("weibo", token.token)
        )
    }

    fun onWeiXinAuthSuccess(token: String) {
        type = 2
        mPresent.getDataByPost(
            0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, ACCOUNT_SAFE_ACT)
                .addParam("type", "2")
                .addParam("wx", token)
        )
    }

    fun onQQAuthSuccess(token: String) {
        type = 1
        mPresent.getDataByPost(
            0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, ACCOUNT_SAFE_ACT)
                .addParam("type", "1")
                .addParam("qq", token)
        )
    }

    override fun onComplete(p0: Any?) {
        if (p0 != null) {
            onQQAuthSuccess(p0.toString())
        }
    }

    override fun onCancel() {
        toast("QQ绑定取消")
    }

    override fun onError(p0: UiError?) {
        toast("QQ绑定失败")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.onActivityResultData(requestCode, resultCode, data, this)
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x0) {
            getFastProgressDialog("正在绑定……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    toast("绑定成功")
                    when (type) {
                        1 -> {
                            UserInfo.user_qq = "1"
                            mIvAccountSafeQQ.setImageResource(R.drawable.img_qq_select_icon)
                            mTvAccountSafeQQ.text = "已绑定"
                        }
                        2 -> {
                            UserInfo.user_wx = "1"
                            mIvAccountSafeWX.setImageResource(R.drawable.img_wx_select_icon)
                            mTvAccountSafeWX.text = "已绑定"
                        }
                        3 -> {
                            UserInfo.user_weibo = "1"
                            mIvAccountSafeWB.setImageResource(R.drawable.img_wb_select_icon)
                            mTvAccountSafeWB.text = "已绑定"
                        }
                    }
                } else {
                    authFail()
                }
            } else {
                authFail()
            }
        }
    }

    private fun authFail() {
        toast("绑定失败")
        when (type) {
            1 -> {
                mIvAccountSafeQQ.setImageResource(R.drawable.img_qq_unselect_icon)
                mTvAccountSafeQQ.text = "未绑定"
            }
            2 -> {
                mIvAccountSafeWX.setImageResource(R.drawable.img_wx_unselect_icon)
                mTvAccountSafeWX.text = "未绑定"
            }
            3 -> {
                mIvAccountSafeWB.setImageResource(R.drawable.img_wb_unselect_icon)
                mTvAccountSafeWB.text = "未绑定"
            }
        }
    }

    override fun onDestroyView() {
        unsubscribe(wxAuthSubscription)
        super.onDestroyView()
    }

}