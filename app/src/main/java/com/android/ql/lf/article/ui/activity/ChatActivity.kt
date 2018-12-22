package com.android.ql.lf.article.ui.activity

import android.os.Build
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.fragments.community.ChatFragment
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_chat_layout.*

/**
 * Created by lf on 18.11.7.
 * @author lf on 18.11.7
 */
class ChatActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_chat_layout

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setSupportActionBar(mTlChatTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mTlChatTitle.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
        mTlChatTitle.setNavigationOnClickListener { finish() }
        supportFragmentManager.beginTransaction().replace(R.id.mFlChatContainer, ChatFragment()).commit()
    }
}