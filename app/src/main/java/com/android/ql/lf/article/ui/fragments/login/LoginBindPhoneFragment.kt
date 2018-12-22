package com.android.ql.lf.article.ui.fragments.login

import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.jsonToUserInfo
import com.android.ql.lf.article.data.postUserInfo
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_login_bind_phone_layout.*
import kotlinx.android.synthetic.main.layout_pre_step.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class LoginBindPhoneFragment : LoginSecondStepFragment() {

    private var mCode: Int = 0

    private val uid by lazy {
        PreferenceUtils.getPrefInt(mContext, "third_login_uid", 0)
    }

    override fun getLayoutId() = R.layout.fragment_login_bind_phone_layout

    override fun initView(view: View?) {
        mTvPreFirstStep.setOnClickListener {
            (parentFragment as LoginFragment).positionFragment(0)
        }
        mTvLoginUserVerCode.setOnClickListener {
            if (mEtLoginUserPhone.isEmpty()) {
                toast("请输入手机号")
                return@setOnClickListener
            }
            if (mEtLoginUserPhone.isNotPhone()) {
                toast("请输入合法的手机号")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x0,
                getBaseParamsWithModAndAct(LOGIN_MODULE, SMSCODE_ACT).addParam(
                    "phone",
                    mEtLoginUserPhone.getTextString()
                )
            )
            mTvLoginUserVerCode.start()
        }
        mBtFirstStepLogin.setOnClickListener {
            if (mEtLoginUserPhone.isEmpty()) {
                toast("请输入手机号")
                return@setOnClickListener
            }
            if (mEtLoginUserPhone.isNotPhone()) {
                toast("请输入合法的手机号")
                return@setOnClickListener
            }
            if (mEtLoginUserVerCode.isEmpty()) {
                toast("请输入验证码")
                return@setOnClickListener
            }
            if ("$mCode" != mEtLoginUserVerCode.getTextString()) {
                toast("请输入正确的验证码")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x1,
                getBaseParamsWithModAndAct(LOGIN_MODULE, PERFECT_ACT)
                    .addParam("phone",mEtLoginUserPhone.getTextString())
                    .addParam("code", mEtLoginUserVerCode.getTextString())
                    .addParam("sex",sex)
                    .addParam("uid",uid)
                    .addParam("birthday",birthday)
                    .addParam("classify",classify)
            )
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x0) {
            getFastProgressDialog("正在获取验证码……")
        } else if (requestID == 0x1) {
            getFastProgressDialog("正在登录……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        when (requestID) {
            0x0 -> {
                val json = JSONObject(result as String)
                json.optInt("status").let {
                    if (it == 200) {
                        toast("验证码发送成功，请注意查收")
                        mCode = json.optInt("code")
                    }
                }
            }
            0x1 -> try {
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        val jsonObject = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                        if (UserInfo.jsonToUserInfo(jsonObject)) {
                            UserInfo.postUserInfo()
                            finish()
                        }else{
                            toast("登录失败")
                        }
                    }
                } else {
                    toast("登录失败")
                }
            } catch (e: Exception) {
                toast("登录失败")
            }
        }
    }

    override fun onDestroyView() {
        mTvLoginUserVerCode.cancel()
        super.onDestroyView()
    }
}