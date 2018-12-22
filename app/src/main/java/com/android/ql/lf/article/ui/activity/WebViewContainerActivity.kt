package com.android.ql.lf.article.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.webkit.*
import com.alipay.sdk.app.PayTask
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.ui.fragments.mine.FeedBackFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_web_view_container_layout.*
import org.jetbrains.anko.toast


class WebViewContainerActivity : BaseActivity() {

    companion object {
        fun startWebViewContainerActivity(context: Context, url: String? = "") {
            val intent = Intent(context, WebViewContainerActivity::class.java)
            intent.putExtra("title", "")
            intent.putExtra("url", url)
            context.startActivity(intent)
        }

        val SDK_PAY_FLAG = 1
    }

    private var canDirectGoMainIndex = false

    private val url by lazy {
        intent.getStringExtra("url") ?: ""
    }

    private val mHandler = object : Handler(Looper.getMainLooper()){

        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                SDK_PAY_FLAG->{
                    val payResult = PayResult(msg.obj as Map<String, String>)
                    /**
                    对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo = payResult.result// 同步返回需要验证的信息
                    val resultStatus = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        toast("支付成功")
                        mWVArticleWebViewContainer.reload()
                    } else {
                        toast("支付失败")
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    }
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_web_view_container_layout

    @SuppressLint("JavascriptInterface","SetJavaScriptEnabled")
    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        mIvArticleWebViewClose.setOnClickListener { finish() }
        mIvArticleWebViewBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
        mIvArticleWebViewBack.setOnClickListener { onBack() }
        mIvArticleWebViewClose.setOnClickListener { finish() }
        mWVArticleWebViewContainer.setNormalSetting()
        mWVArticleWebViewContainer.addJavascriptInterface(WebViewInterface(), JS_BRIDGE_INTERFACE_NAME)
        mWVArticleWebViewContainer.webViewClient = MyWebViewClient()
        mWVArticleWebViewContainer.webChromeClient = MyChromeWebViewClient()
        mWVArticleWebViewContainer.loadLocalHtml(url)
        when (url) {
            "wallet.html" ->{
                mTvArticleWebViewAction.visibility = View.VISIBLE
                mTvArticleWebViewAction.text = "充值问题"
                mTvArticleWebViewAction.setTextColor(ContextCompat.getColor(this,R.color.colorAccent))
                mTvArticleWebViewAction.setOnClickListener {
                    FeedBackFragment.startFeedBackFragment(this,2)
                }
                mMLlWebViewContainer.registFragmentSizeObserver { wdiff, hdiff ->
                    if (hdiff < -200) { // 处理输入法弹出事件
                        mWVArticleWebViewContainer.loadUrl("""
                   javascript:(function(){
                        $(".withdraw-main").css({'top':'1%'})
                   }())
                """)
                    } else if (hdiff == 0) {  // 处理输入法隐藏事件
                        mWVArticleWebViewContainer.loadUrl("""
                   javascript:(function(){
                        $(".withdraw-main").css({'top':'10%'})
                   }())
                """)
                    }
                }
            }
            else -> {
                mTvArticleWebViewAction.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        onBack()
    }

    private fun onBack() {
        if (canDirectGoMainIndex){
            mWVArticleWebViewContainer.loadLocalHtml(url)
            return
        }
        if (mWVArticleWebViewContainer.canGoBack()) {
            mWVArticleWebViewContainer.goBack()
        } else {
            mWVArticleWebViewContainer.destroy()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mPbArticleProgress.visibility = View.VISIBLE
            mPbArticleProgress.progress = 0
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mPbArticleProgress.visibility = View.GONE
            mTvArticleWebViewTitle.text = view?.title ?: ""
            if (url!=null){
                when {
                    url.endsWith("wallet.html",true) -> {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        mTvArticleWebViewAction.visibility = View.VISIBLE
                        canDirectGoMainIndex = false
                        mWVArticleWebViewContainer.clearHistory()
                    }
                    url.endsWith("backcard.html",true) -> {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        mTvArticleWebViewAction.visibility = View.GONE
                        canDirectGoMainIndex = true
                    }
                    else -> {
                        canDirectGoMainIndex = false
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        mTvArticleWebViewAction.visibility = View.GONE
                    }
                }
            }
        }
    }

    inner class MyChromeWebViewClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            mPbArticleProgress.progress = newProgress
        }

    }

    inner class WebViewInterface {

        @JavascriptInterface
        fun getUserId(): String {
            return UserInfo.user_id.toString()
        }

        @JavascriptInterface
        fun aliPay(sign:String,price:String){
            mHandler.post {
                alert("提示","是否要支付金额${price}元","支付","取消",{_,_->
                    val runnable = Runnable {
                        val payTask = PayTask(this@WebViewContainerActivity)
                        val result = payTask.payV2(sign,true)
                        val msg = Message()
                        msg.what = SDK_PAY_FLAG
                        msg.obj = result
                        mHandler.sendMessage(msg)
                    }
                    val thread = Thread(runnable)
                    thread.start()
                },null)
            }
        }

        @JavascriptInterface
        fun myAlert(content:String){
            runOnUiThread{
                alert("提示",content,"确定","",null,null)
            }
        }

        @JavascriptInterface
        fun myToast(message:String){
            runOnUiThread {
                toast(message)
            }
        }
    }
}