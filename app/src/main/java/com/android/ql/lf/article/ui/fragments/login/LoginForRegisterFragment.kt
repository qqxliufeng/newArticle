package com.android.ql.lf.article.ui.fragments.login

import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_login_for_register_layout.*
import kotlinx.android.synthetic.main.layout_pre_step.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

/**
 * Created by lf on 18.11.24.
 * @author lf on 18.11.24
 */
class LoginForRegisterFragment : BaseNetWorkingFragment(){

    private var mCode: Int = 0

    override fun getLayoutId() = R.layout.fragment_login_for_register_layout

    override fun initView(view: View?) {
        mTvPreFirstStep.setOnClickListener {
            (parentFragment as LoginFragment).positionFragment(2)
        }
        mLlLoginFirstLogin.setOnClickListener {
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
        mBtFirstStepRegister.setOnClickListener {
            if (mEtLoginUserPhone.isEmpty()) {
                toast("请输入手机号")
                return@setOnClickListener
            }
            if (mEtLoginUserPhone.isNotPhone()) {
                toast("请输入合法的手机号")
                return@setOnClickListener
            }
            if (mEtLoginUserVerCode.getTextString() != mCode.toString()){
                toast("验证码不正确")
                return@setOnClickListener
            }
            if (mEtLoginUserPassword.isEmpty()){
                toast("请输入密码")
                return@setOnClickListener
            }
            if (mEtLoginUserPasswordTwo.isEmpty()){
                toast("请再次输入密码")
                return@setOnClickListener
            }
            if (mEtLoginUserPassword.passwordIsNotInRange()){
                toast("请输入6 ~ 16位密码")
                return@setOnClickListener
            }
            if (mEtLoginUserPasswordTwo.getTextString() != mEtLoginUserPassword.getTextString()){
                toast("两次密码不一致")
                return@setOnClickListener
            }
            PreferenceUtils.setPrefString(mContext,"user_phone",mEtLoginUserPhone.getTextString())
            PreferenceUtils.setPrefString(mContext,"user_code",mEtLoginUserVerCode.getTextString())
            PreferenceUtils.setPrefString(mContext,"user_password",mEtLoginUserPassword.getTextString())
            (parentFragment as LoginFragment).positionFragment(1)
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x0) {
            getFastProgressDialog("正在获取验证码……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val json = JSONObject(result as String)
            json.optInt("status").let {
                if (it == 200) {
                    toast("验证码发送成功，请注意查收")
                    mCode = json.optInt("code")
                }
            }
        }
    }
}