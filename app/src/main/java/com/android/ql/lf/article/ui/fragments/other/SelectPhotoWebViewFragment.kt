package com.android.ql.lf.article.ui.fragments.other

import android.net.Uri
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.setNormalSetting
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.select_photo_web_view_layout.*

/**
 * Created by lf on 18.11.20.
 * @author lf on 18.11.20
 */
class SelectPhotoWebViewFragment :BaseNetWorkingFragment(){

    override fun getLayoutId() = R.layout.select_photo_web_view_layout

    override fun initView(view: View?) {
        mWVSelectPhoto.setNormalSetting()
        mWVSelectPhoto.webViewClient = MySelectWebViewClient()
        mWVSelectPhoto.webChromeClient = MySelectChromeWebViewClient()
    }

    inner class MySelectWebViewClient : WebViewClient(){}

    inner class MySelectChromeWebViewClient : WebChromeClient(){
        override fun onShowFileChooser(webView: WebView?,filePathCallback: ValueCallback<Array<Uri>>?,fileChooserParams: FileChooserParams?): Boolean {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }
    }

}