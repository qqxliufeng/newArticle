package com.android.ql.lf.article.ui.fragments.login

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseViewPagerFragment

class LoginFragment : BaseViewPagerFragment() {

    companion object {
        fun startLoginFragment(context: Context) {
            FragmentContainerActivity.from(context).setTitle("").setNeedNetWorking(false).setHiddenToolBar(true).setClazz(LoginFragment::class.java).start()
        }
    }

    private val loginFragment by lazy { LoginForSMSCodeFragment() }

    override fun initView(view: View?) {
        super.initView(view)
        (mContext as FragmentContainerActivity).setOnBackPressListener {
            if (noScrollViewPager?.currentItem == 0) {
                finish()
                false
            }else{
                noScrollViewPager.currentItem = 0
                true
            }
        }
    }

    override fun getViewPagerAdapter() = object : FragmentPagerAdapter(childFragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    loginFragment
                }
                1 -> {
                    LoginSecondStepFragment()
                }
                2 -> {
                    LoginForPasswordFragment()
                }
                3 -> {
                    LoginForRegisterFragment()
                }
                4 -> {
                    LoginBindPhoneFragment()
                }
                else -> {
                    LoginForSMSCodeFragment()
                }
            }
        }

        override fun getCount() = 5
    }




    override fun onMyActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onMyActivityResult(requestCode, resultCode, data)
        loginFragment.onMyActivityResult(requestCode,resultCode,data)
    }

    override fun positionFailed(position: Int) {
        finish()
    }
}