package com.android.ql.lf.article.ui.fragments.other

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.setNormalSetting
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.fragment_article_web_view_layout.*
import org.jetbrains.anko.bundleOf

class NetWebViewFragment : BaseNetWorkingFragment() {

    companion object {
        fun startNetWebViewFragment(context: Context, url: String) {
            FragmentContainerActivity.from(context).setNeedNetWorking(true).setTitle("")
                .setClazz(NetWebViewFragment::class.java)
                .setExtraBundle(bundleOf(Pair("url", url))).start()
        }
    }

    override fun getLayoutId() = R.layout.fragment_article_web_view_layout

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun initView(view: View?) {
        mWVArticleWebViewContainer.setNormalSetting()
        mWVArticleWebViewContainer.webViewClient = MyWebViewClient()
        mWVArticleWebViewContainer.webChromeClient = MyChromeWebViewClient()
        val url = arguments?.getString("url") ?: ""
        mWVArticleWebViewContainer.loadUrl(url)
    }

    override fun onDestroyView() {
        mWVArticleWebViewContainer.destroy()
        super.onDestroyView()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mPbArticleProgress?.visibility = View.VISIBLE
            mPbArticleProgress?.progress = 0
        }


        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mPbArticleProgress?.visibility = View.GONE
//            mTvArticleWebViewTitle.text = view?.title ?: ""
        }

    }

    inner class MyChromeWebViewClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            mPbArticleProgress?.progress = newProgress
        }

    }
}