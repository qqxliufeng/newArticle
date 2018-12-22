package com.android.ql.lf.article.ui.fragments.start

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.UserInfoLiveData
import com.android.ql.lf.article.ui.activity.MainActivity
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import com.android.ql.lf.article.utils.alert
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import com.android.ql.lf.baselibaray.utils.RxBus
import kotlinx.android.synthetic.main.fragment_select_user_info_layout.*
import java.util.*

class SelectUserInfoFragment : BaseNetWorkingFragment() {

    override fun getLayoutId() = R.layout.fragment_select_user_info_layout

    override fun initView(view: View?) {
        UserInfoLiveData.observe(this, Observer<UserInfo> {
            startActivity(Intent(mContext,MainActivity::class.java))
            finish()
        })
        mTvSelectUserInfoJump.setOnClickListener {
            alert("是否要跳过？","跳过","不跳过"){_,_->
                PreferenceUtils.setPrefString(mContext, "sex","")
                PreferenceUtils.setPrefString(mContext, "birthday","")
                PreferenceUtils.setPrefBoolean(mContext,"just_jump",true)
                startActivity(Intent(mContext,MainActivity::class.java))
                finish()
            }
        }
        mTvSelectUserInfoAge.text = "${Calendar.getInstance().get(Calendar.YEAR)}-${Calendar.getInstance().get(Calendar.MONTH)}-${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}"
        mTvSelectUserInfoAge.setOnClickListener {
            val datePickDialog = DatePickerDialog(mContext, { _, year, month, day ->
                mTvSelectUserInfoAge.text = "$year-$month-$day"
                PreferenceUtils.setPrefString(mContext, "birthday","$year-$month-$day")
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            datePickDialog.show()
        }
        mBtSelectUserInfoNextStep.setOnClickListener {
            (parentFragment as StartCustomTypeFragment).positionFragment(1)
        }
        mRbSelectUserInfoSexWoman.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                PreferenceUtils.setPrefString(mContext, "sex","女")
            }
        }
        mRbSelectUserInfoSexMan.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                PreferenceUtils.setPrefString(mContext, "sex","男")
            }
        }
        mTvSelectUserInfoLogin.setOnClickListener {
            PreferenceUtils.setPrefString(mContext, "sex","")
            PreferenceUtils.setPrefString(mContext, "birthday","")
            PreferenceUtils.setPrefBoolean(mContext,"just_jump",true)
            LoginFragment.startLoginFragment(mContext)
        }
    }
}