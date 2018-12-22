package com.android.ql.lf.article.ui.fragments.login

import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.jsonToUserInfo
import com.android.ql.lf.article.data.postUserInfo
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_login_for_password_layout.*
import kotlinx.android.synthetic.main.layout_pre_step.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

/**
 * Created by lf on 18.11.24.
 * @author lf on 18.11.24
 */
class LoginForPasswordFragment : BaseNetWorkingFragment() {

    override fun getLayoutId() = R.layout.fragment_login_for_password_layout

    override fun initView(view: View?) {
        mTvPreFirstStep.setOnClickListener {
            (parentFragment as LoginFragment).positionFragment(0)
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
            if (mEtLoginUserPassword.isEmpty()) {
                toast("请输入密码")
                return@setOnClickListener
            }
            if (mEtLoginUserPassword.passwordIsNotInRange()) {
                toast("请输入6 ~ 16位数密码")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x0,
                getBaseParamsWithModAndAct(LOGIN_MODULE, LOGINDO_ACT)
                    .addParam("phone",mEtLoginUserPhone.getTextString())
                    .addParam("pass", mEtLoginUserPassword.getTextString())
            )
        }
        mLlLoginFirstStepRegister.setOnClickListener {
            (parentFragment as LoginFragment).positionFragment(3)
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x0) {
            getFastProgressDialog("正在登录……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            try {
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        val jsonObject = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                        if (UserInfo.jsonToUserInfo(jsonObject)) {
                            UserInfo.postUserInfo()
                            finish()
                        } else {
                            toast("登录失败")
                        }
                    }else{
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                } else {
                    toast("登录失败")
                }
            } catch (e: Exception) {
                toast("登录失败")
            }
        }
    }
}