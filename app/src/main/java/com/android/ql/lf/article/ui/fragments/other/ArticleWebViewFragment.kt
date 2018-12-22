package com.android.ql.lf.article.ui.fragments.other

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.EditText
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleItem
import com.android.ql.lf.article.data.ArticleType
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.isLogin
import com.android.ql.lf.article.ui.activity.ArticleEditActivity
import com.android.ql.lf.article.ui.fragments.article.ArticleInfoDisplayFragment
import com.android.ql.lf.article.ui.fragments.article.ArticleInfoForNormalFragment
import com.android.ql.lf.article.ui.fragments.article.ArticleInfoForTrashFragment
import com.android.ql.lf.article.ui.fragments.article.Classify
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import com.android.ql.lf.article.ui.fragments.mine.PersonalIndexFragment
import com.android.ql.lf.article.ui.widgets.PopupWindowDialog
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.RxBus
import com.android.ql.lf.baselibaray.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_article_web_view_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

class ArticleWebViewFragment : BaseNetWorkingFragment(), FragmentContainerActivity.OnBackPressListener {

    companion object {
        fun startArticleWebViewFragment(context: Context, title: String, url: String, type: Int) {
            FragmentContainerActivity.from(context).setNeedNetWorking(false).setTitle(title)
                .setClazz(ArticleWebViewFragment::class.java)
                .setExtraBundle(bundleOf(Pair("url", url), Pair("title", title),Pair("type", type))).start()
        }
    }

    private val switch by lazy { SwitchCompat(mContext) }

    private val updateArticleListSubscription by lazy {
        RxBus.getDefault().toObservable(ArticleItem::class.java).subscribe {
           mWVArticleWebViewContainer.reload()
        }
    }

    override fun getLayoutId() = R.layout.fragment_article_web_view_layout

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun initView(view: View?) {
        if (mContext is FragmentContainerActivity) {
            (mContext as FragmentContainerActivity).setOnBackPressListener(this)
        }
        updateArticleListSubscription
        mWVArticleWebViewContainer.setNormalSetting()
        mWVArticleWebViewContainer.addJavascriptInterface(WebViewInterface(), JS_BRIDGE_INTERFACE_NAME)

        mWVArticleWebViewContainer.webViewClient = MyWebViewClient()
        mWVArticleWebViewContainer.webChromeClient = MyChromeWebViewClient()
        val url = arguments?.getString("url") ?: ""
        mWVArticleWebViewContainer.loadLocalHtml(url)
        when (ArticleType.getTypeNameById(arguments?.getInt("type") ?: 0)) {
            ArticleType.PRIVATE_ARTICLE -> {
                switch.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        toast("已经开启编辑模式\n点击文章进入编辑器")
                    } else {
                        toast("已经开启预览模式\n点击文章进入预览")
                    }
                }
                val layoutParams =
                    Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.RIGHT
                (mContext as FragmentContainerActivity).toolbar.addView(switch, layoutParams)
            }
            ArticleType.PUBLIC_ARTICLE -> {
            }
            ArticleType.COLLECTION_ARTICLE -> {
            }
            ArticleType.POST_ARTICLE -> {
            }
            else -> {
            }
        }
    }

    override fun onDestroyView() {
        mWVArticleWebViewContainer.destroy()
        unsubscribe(updateArticleListSubscription)
        super.onDestroyView()
    }

    override fun onBackPress(): Boolean {
        onBack()
        return true
    }

    private fun onBack() {
        if (mWVArticleWebViewContainer.canGoBack()) {
            mWVArticleWebViewContainer.goBack()
        } else {
            finish()
        }
    }

    override fun onRequestStart(requestID: Int) {
        when(requestID){
            0x0->{
                getFastProgressDialog("正在回复……")
            }
            0x1->{
                getFastProgressDialog("正在评论……")
            }
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        when(requestID){
            0x0->{
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("回复成功")
                    }else{
                        toast("回复失败……")
                    }
                }else{
                    toast("回复失败……")
                }
            }
            0x1->{
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("评论成功")
                        mWVArticleWebViewContainer.reload()
                    }else{
                        toast("评论失败……")
                    }
                }else{
                    toast("评论失败……")
                }
            }
        }
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

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            view?.loadUrl(request?.url?.path)
            return super.shouldOverrideUrlLoading(view, request)
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

    /**
     * 用于Java/Kotlin和JavaScript相互调用的接口类
     */
    inner class WebViewInterface {

        /**
         * 获取用户ID
         */
        @JavascriptInterface
        fun getUserId(): String {
            return UserInfo.user_id.toString()
        }

        /**
         * 获取文章类型
         */
        @JavascriptInterface
        fun getArticleType():Int{
            return arguments?.getInt("type") ?: 0
        }

        /**
         * 根据不同的类型，跳转到不同的页面
         */
        @JavascriptInterface
        fun jump(type: Int,aid:String,title: String,content:String) {
            if (TextUtils.isEmpty(aid)){
                return
            }
            mContext.runOnUiThread {
                if (UserInfo.isLogin()){
                    when (ArticleType.getTypeNameById(type)) {
                        ArticleType.PRIVATE_ARTICLE -> {
                            if (switch.isChecked){
                                //进入编辑模式
                                ArticleEditActivity.startArticleEditActivity(mContext,
                                    title,
                                    content,
                                    Classify(0,"",false,""),
                                    1,
                                    aid.toInt())
                            }else{
                                //进入预览模式
                                ArticleInfoDisplayFragment.startArticleInfoDisplayFragment(mContext,aid.toInt(),UserInfo.user_id)
                            }
                        }
                        ArticleType.PUBLIC_ARTICLE -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(mContext,aid.toInt(),UserInfo.user_id)
                        }
                        ArticleType.COLLECTION_ARTICLE -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(mContext,aid.toInt(),UserInfo.user_id)
                        }
                        ArticleType.POST_ARTICLE -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(mContext,aid.toInt(),UserInfo.user_id)
                        }
                        ArticleType.TRASH_ARTICLE->{
                            ArticleInfoForTrashFragment.startArticleInfoForTrashFragment(mContext,aid.toInt(),UserInfo.user_id)
                        }
                        else -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(mContext,aid.toInt(),UserInfo.user_id)
                        }
                    }
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
        }

        /**
         * 用于消息收藏中的跳转
         */
        @JavascriptInterface
        fun myJump(type: String,aid:String){
            if (TextUtils.isEmpty(aid)){
                return
            }
            mContext.runOnUiThread {
                if (UserInfo.isLogin()) {
                    when (ArticleType.getTypeNameById(type.toInt())) {
                        ArticleType.PRIVATE_ARTICLE -> {
                            ArticleInfoDisplayFragment.startArticleInfoDisplayFragment(
                                mContext,
                                aid.toInt(),
                                UserInfo.user_id
                            )
                        }
                        ArticleType.PUBLIC_ARTICLE -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(
                                mContext,
                                aid.toInt(),
                                UserInfo.user_id
                            )
                        }
                        ArticleType.TRASH_ARTICLE -> {
                            ArticleInfoForTrashFragment.startArticleInfoForTrashFragment(
                                mContext,
                                aid.toInt(),
                                UserInfo.user_id
                            )
                        }
                        else -> {
                            ArticleInfoForNormalFragment.startArticleInfoForNormal(
                                mContext,
                                aid.toInt(),
                                UserInfo.user_id
                            )
                        }
                    }
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
        }

        @JavascriptInterface
        fun startPersonalIndexById(id:String){
            if (TextUtils.isEmpty(id)){
                return
            }
            mContext.runOnUiThread {
                PersonalIndexFragment.startPersonalIndexFragment(mContext,id.toInt())
            }
        }

        /**
         * 通过文章ID 获取文章内容
         */
        @JavascriptInterface
        fun getContentById(id:Int){
        }


        /**
         * 弹出回复框
         */
        @JavascriptInterface
        fun showReplyDialog(cuid:String,reid:String){
            if (TextUtils.isEmpty(cuid)){
                return
            }
            mContext.runOnUiThread {
                val contentView = View.inflate(mContext,R.layout.dialog_article_comment_layout,null)
                val popUpWindow = PopupWindowDialog.showReplyDialog(mContext,contentView)
                val submit = contentView.findViewById<TextView>(R.id.mTvArticleCommentSubmit)
                val content = contentView.findViewById<EditText>(R.id.mEtArticleCommentContent)
                submit.text = "发表回复"
                submit.setOnClickListener {
                    if (content.isEmpty()){
                        toast("请输入回复内容")
                        return@setOnClickListener
                    }
                    popUpWindow.dismiss()
                    mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MESSAGE_MODULE, COMMENT_REPLY_ACT)
                        .addParam("cuid",cuid)
                        .addParam("content",content.getTextString())
                        .addParam("reid",reid)
                        .addParam("type","2"))
                }
                contentView.postDelayed({
                    PopupWindowDialog.toggleSoft(mContext)
                },100)
            }
        }


        /**
         * 弹出评论回复对话框
         */
        @JavascriptInterface
        fun showCommentDialig(cuid:String,reid:String){
            if (TextUtils.isEmpty(cuid) || TextUtils.isEmpty(reid)){
                return
            }
            mContext.runOnUiThread {
                val contentView = View.inflate(mContext,R.layout.dialog_comment_layout,null)
                val popUpWindow = PopupWindowDialog.showReplyDialog(mContext,contentView)
                val submit = contentView.findViewById<TextView>(R.id.mTvArticleCommentSubmit)
                val content = contentView.findViewById<EditText>(R.id.mEtArticleCommentContent)
                submit.setOnClickListener {
                    if (content.isEmpty()){
                        toast("请输入评论内容")
                        return@setOnClickListener
                    }
                    popUpWindow.dismiss()
                    mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MESSAGE_MODULE, COMMENT_REPLY_ACT)
                        .addParam("cuid",cuid)
                        .addParam("content",content.getTextString())
                        .addParam("reid",reid)
                        .addParam("type","2"))
                }
                contentView.post {
                    PopupWindowDialog.toggleSoft(mContext)
                }
            }
        }


        /**
         * 浏览历史，弹出对话框
         */
        @JavascriptInterface
        fun showHistoryDeleteDialog(id:String){
            mContext.runOnUiThread {
                alert("提示","是否要删除此记录？","删除","不删除",{_,_->
                    mWVArticleWebViewContainer.loadUrl("""javascript:del($id)""")
                },null)
            }
        }

        @JavascriptInterface
        fun sendMessageUpdateFocus(messageFlag:String){
            mContext.runOnUiThread {
                RxBus.getDefault().post(messageFlag)
            }
        }
    }
}