package com.android.ql.lf.article.ui.fragments.mine

import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.fragment_reset_password_layout.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

/**
 * Created by lf on 18.11.27.
 * @author lf on 18.11.27
 */
class ResetPasswordFragment : BaseNetWorkingFragment() {

    override fun getLayoutId() = R.layout.fragment_reset_password_layout

    override fun initView(view: View?) {
        mBtResetPassword.setOnClickListener {
            if (mEtResetPasswordOldPassword.isEmpty()) {
                toast("请输入原始密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordOldPassword.passwordIsNotInRange()) {
                toast("请输入6 ~ 16位原始密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordNewPassword.isEmpty()) {
                toast("请输入新密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordNewPassword.passwordIsNotInRange()) {
                toast("请输入6 ~ 16位新密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordNewPasswordTwo.isEmpty()) {
                toast("请再次输入新密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordNewPasswordTwo.passwordIsNotInRange()) {
                toast("请再次输入6 ~ 16位新密码")
                return@setOnClickListener
            }
            if (mEtResetPasswordNewPasswordTwo.getTextString() != mEtResetPasswordNewPassword.getTextString()) {
                toast("两次密码不一致")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x0, getBaseParamsWithModAndAct(LOGIN_MODULE, RESET_PASSWORD_ACT)
                    .addParam("password", mEtResetPasswordOldPassword.getTextString())
                    .addParam("repassword", mEtResetPasswordNewPassword.getTextString())
            )
        }
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x0) {
            getFastProgressDialog("正在重置密码……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    toast("密码重置成功，请牢记")
                    finish()
                } else {
                    toast((check.obj as JSONObject).optString(MSG_FLAG))
                }
            } else {
                toast("密码重置失败，请重试……")
            }
        }
    }

}