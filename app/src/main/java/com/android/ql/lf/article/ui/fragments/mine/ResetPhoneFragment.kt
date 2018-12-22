package com.android.ql.lf.article.ui.fragments.mine

import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.fragment_reset_phone_layout.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class ResetPhoneFragment : BaseNetWorkingFragment(){


    private var mCode: Int = 0

    override fun getLayoutId() = R.layout.fragment_reset_phone_layout

    override fun initView(view: View?) {
        mTvResetUserVerCode.setOnClickListener {
            if (mEtResetUserPhone.isEmpty()){
                toast("请输入手机号")
                return@setOnClickListener
            }
            if (mEtResetUserPhone.isNotPhone()){
                toast("请输入正确的手机号")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x1,
                getBaseParamsWithModAndAct(LOGIN_MODULE, SMSCODE_ACT).addParam(
                    "phone",
                    mEtResetUserPhone.getTextString()
                )
            )
            mTvResetUserVerCode.start()
        }
        mBtResetPhone.setOnClickListener {
            if (mEtResetUserPhone.isEmpty()){
                toast("请输入手机号")
                return@setOnClickListener
            }
            if (mEtResetUserPhone.isNotPhone()){
                toast("请输入正确的手机号")
                return@setOnClickListener
            }
            if (mEtResetUserVerCode.isEmpty()){
                toast("请输入验证码")
                return@setOnClickListener
            }
            if (mEtResetUserVerCode.getTextString() != mCode.toString()){
                toast("请输入正确的验证码")
                return@setOnClickListener
            }
            mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_EDIT_ACT)
                .addParam("phone",mEtResetUserPhone.getTextString())
                .addParam("code",mEtResetUserVerCode.getTextString()))
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x0){
            getFastProgressDialog("正在设置……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            val check = checkResultCode(result)
            if (check!=null) {
                if (check.code == SUCCESS_CODE){
                    toast("手机号设置成功")
                    UserInfo.user_phone = mEtResetUserPhone.getTextString()
                    finish()
                }else{
                    toast((check.obj as JSONObject).optString(MSG_FLAG))
                }
            }else{
                toast("设置失败")
            }
        }else if (requestID == 0x1){
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