package com.android.ql.lf.article.ui.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.*
import com.android.ql.lf.article.ui.fragments.article.SelectTypeFragment
import com.android.ql.lf.article.ui.fragments.bottom.FocusFragment
import com.android.ql.lf.article.ui.fragments.bottom.IndexFragment
import com.android.ql.lf.article.ui.fragments.bottom.MessageFragment
import com.android.ql.lf.article.ui.fragments.bottom.MineFragment
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class MainActivity : BaseActivity() {

    private val bottomIconUnSelect = arrayListOf(R.drawable.index_un_select, R.drawable.focus_un_select, R.drawable.bottom_center_icon, R.drawable.message_un_select, R.drawable.mine_un_select)
    private val bottomIconSelect = arrayListOf(R.drawable.index_select, R.drawable.focus_select, R.drawable.bottom_center_icon, R.drawable.message_select, R.drawable.mine_select)

    private val checkTextColor by lazy {
        ContextCompat.getColor(this, R.color.checkTextColor)
    }

    private val normalTextColor by lazy {
        ContextCompat.getColor(this, R.color.normalTextColor)
    }

    private val mineFragment by lazy { MineFragment() }

    private var exists = 0L


    override fun getLayoutId() = R.layout.activity_main

    private var tempTab : TabLayout.Tab? = null

    override fun initView() {
        UserInfoLiveData.observe(this, Observer<UserInfo> {
            onLogin()
        })
        statusBarColor = Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        (mVpMainContainer.parent as ConstraintLayout).setPadding(0, statusHeight, 0, 0)
        initBottomTab()
        mVpMainContainer.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        IndexFragment()
                    }
                    1->{
                        FocusFragment()
                    }
                    2 -> {
                        MessageFragment()
                    }
                    3->{
                        mineFragment
                    }
                    else -> {
                        IndexFragment()
                    }
                }
            }
            override fun getCount() = 4
        }
        mVpMainContainer.offscreenPageLimit = 4
        mTlMainBottom.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tempTab = tab
                if (UserInfo.isLogin()){
                    (0 until mTlMainBottom.tabCount).map {
                        mTlMainBottom.getTabAt(it)
                    }.forEachWithIndex { i, it ->
                        if (tab == it) {
                            it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = checkTextColor
                            it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconSelect[i])
                        } else {
                            it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
                            it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[i])
                        }
                    }
                    if (tab?.tag == 2) {
                        if (mVpMainContainer.currentItem > 1) {
                            mTlMainBottom.getTabAt(mVpMainContainer.currentItem + 1)?.select()
                        } else {
                            mTlMainBottom.getTabAt(mVpMainContainer.currentItem)?.select()
                        }
                        if (UserInfo.isLogin()) {
                            SelectTypeFragment.startSelectTypeFragment(this@MainActivity, "选择文章所属类别")
                        }else{
                            LoginFragment.startLoginFragment(this@MainActivity)
                        }
                        return
                    }
                    if (tab?.tag == 1){ // 点击关注的时候   取消红点提示
                        UserInfo.user_likeStatus = -1
                        setRedNotify()
                    }
                    var index = tab?.position!!
                    if (index > 2) {
                        index = tab.position - 1
                    }
                    mVpMainContainer.currentItem = index
                }else{
                    if (tab?.position == 0){
                        return
                    }
                    LoginFragment.startLoginFragment(this@MainActivity)
                }
            }
        })
        tempTab = mTlMainBottom.getTabAt(0)
    }

    override fun onResume() {
        super.onResume()
        navigationToIndex()
    }

    fun navigationToIndex(){
        if (!UserInfo.isLogin()){
            mVpMainContainer.currentItem = 0
            (0 until mTlMainBottom.tabCount).map {
                mTlMainBottom.getTabAt(it)
            }.forEachWithIndex { i, it ->
                if (i == 0) {
                    it?.select()
                    it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = checkTextColor
                    it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconSelect[i])
                } else {
                    it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
                    it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[i])
                }
            }
        }
    }

    fun onLogin(){
        setRedNotify()
        if (tempTab?.tag == 2){
            SelectTypeFragment.startSelectTypeFragment(this@MainActivity, "选择文章所属类别")
        }else {
            mVpMainContainer.currentItem = tempTab?.position ?: 0
            tempTab?.select()
            (0 until mTlMainBottom.tabCount).map {
                mTlMainBottom.getTabAt(it)
            }.forEachWithIndex { i, it ->
                if (tempTab == it) {
                    it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = checkTextColor
                    it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)
                        ?.setImageResource(bottomIconSelect[i])
                } else {
                    it?.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
                    it?.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)
                        ?.setImageResource(bottomIconUnSelect[i])
                }
            }
        }
    }

    fun setRedNotify(){
        val focusRed = mTlMainBottom.getTabAt(1)?.customView?.findViewById<TextView>(R.id.mNotifyView)
        val messageRed = mTlMainBottom.getTabAt(3)?.customView?.findViewById<TextView>(R.id.mNotifyView)
        if (UserInfo.user_likeStatus > 0){
            focusRed?.visibility = View.VISIBLE
        }else{
            focusRed?.visibility = View.GONE
        }
        if (UserInfo.user_allStatus > 0){
            messageRed?.visibility = View.VISIBLE
        }else{
            messageRed?.visibility = View.GONE
        }
    }

    private fun initBottomTab() {
        val indexTab = mTlMainBottom.newTab()
        indexTab.customView = View.inflate(this, R.layout.bottom_item_layout, null)
        indexTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.text = "首页"
        indexTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = checkTextColor
        indexTab.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconSelect[0])
        indexTab.tag = 0

        val focusTab = mTlMainBottom.newTab()
        focusTab.customView = View.inflate(this, R.layout.bottom_item_layout, null)
        focusTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.text = "关注"
        focusTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
        focusTab.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[1])
        focusTab.tag = 1

        val articleEditTab = mTlMainBottom.newTab()
        articleEditTab.tag = 2
        articleEditTab.customView = View.inflate(this, R.layout.bottom_item_layout, null)
        articleEditTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.visibility = View.GONE
        articleEditTab.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[2])

        val messageTab = mTlMainBottom.newTab()
        messageTab.customView = View.inflate(this, R.layout.bottom_item_layout, null)
        messageTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.text = "消息"
        messageTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
        messageTab.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[3])
        messageTab.tag = 3

        val mineTab = mTlMainBottom.newTab()
        mineTab.customView = View.inflate(this, R.layout.bottom_item_layout, null)
        mineTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.text = "我的"
        mineTab.customView?.findViewById<TextView>(R.id.mTextBottomTitle)?.textColor = normalTextColor
        mineTab.customView?.findViewById<ImageView>(R.id.mImageBottomIcon)?.setImageResource(bottomIconUnSelect[4])
        mineTab.tag = 4

        mTlMainBottom.addTab(indexTab)
        mTlMainBottom.addTab(focusTab)
        mTlMainBottom.addTab(articleEditTab)
        mTlMainBottom.addTab(messageTab)
        mTlMainBottom.addTab(mineTab)
        indexTab.select()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null) {
            mineFragment.onWeiboShareResult(data)
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exists > 2000){
            toast("再按一次退出")
            exists = System.currentTimeMillis()
        }else {
            UserInfo.clearUserInfo()
            super.onBackPressed()
        }
    }
}
