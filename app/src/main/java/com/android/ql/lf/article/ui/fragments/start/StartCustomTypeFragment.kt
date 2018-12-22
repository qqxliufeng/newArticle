package com.android.ql.lf.article.ui.fragments.start

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseViewPagerFragment

class StartCustomTypeFragment : BaseViewPagerFragment() {

    companion object {
        fun startCustomInfoFragment(context: Context) {
            FragmentContainerActivity.from(context).setClazz(StartCustomTypeFragment::class.java).setHiddenToolBar(true).setNeedNetWorking(false).start()
        }
    }

    override fun getViewPagerAdapter() = object : FragmentPagerAdapter(childFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    SelectUserInfoFragment()
                }
                1 -> {
                    SelectUserLikeTypeFragment()
                }
                else -> {
                    SelectUserInfoFragment()
                }
            }
        }

        override fun getCount() = 2
    }

    override fun positionFailed(position: Int) {
        finish()
    }

}