package com.android.ql.lf.article.ui.activity

import com.android.ql.lf.article.R
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import com.android.ql.lf.baselibaray.utils.BaseConfig
import kotlinx.android.synthetic.main.test_activity_layout.*

/**
 * Created by lf on 18.11.28.
 * @author lf on 18.11.28
 */
class TestActivity : BaseActivity(){

    override fun getLayoutId() = R.layout.test_activity_layout

    override fun initView() {
        mTestWv.settings.allowUniversalAccessFromFileURLs = true
        mTestWv.settings.allowFileAccessFromFileURLs = true
        mTestWv.settings.allowFileAccess = true
        mTestWv.loadDataWithBaseURL(null,"""<img src="file://${BaseConfig.IMAGE_PATH}1543326907707.jpg" style="width:100%"/>""","text/html","UTF-8",null)
    }
}