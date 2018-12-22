package com.android.ql.lf.article.ui.fragments.article

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.view.menu.MenuBuilder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.*
import com.android.ql.lf.article.ui.activity.ArticleEditActivity
import com.android.ql.lf.article.ui.activity.WebViewContainerActivity
import com.android.ql.lf.article.ui.fragments.bottom.FocusFragment
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import com.android.ql.lf.article.ui.fragments.mine.MyFriendListFragment
import com.android.ql.lf.article.ui.fragments.mine.PersonalIndexFragment
import com.android.ql.lf.article.ui.fragments.other.ArticleWebViewFragment
import com.android.ql.lf.article.ui.fragments.other.NetWebViewFragment
import com.android.ql.lf.article.ui.fragments.share.ArticleShareDialogFragment
import com.android.ql.lf.article.ui.fragments.share.ImagePosterShareFragment
import com.android.ql.lf.article.ui.widgets.CommentLinearLayout
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.article.ui.widgets.PopupWindowDialog
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.utils.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.fragment_article_info_for_normal_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class ArticleInfoForNormalFragment : BaseRecyclerViewFragment<ArticleCommentItem>() {

    companion object {

        const val UPDATE_ARTICLE_FLAG = "update_article"

        fun startArticleInfoForNormal(context: Context, aid: Int, auid: Int = 0, like: Int = 0, love: Int = 0) {
            FragmentContainerActivity.from(context).setTitle("详情").setNeedNetWorking(true)
                .setExtraBundle(
                    bundleOf(
                        Pair("aid", aid),
                        Pair("auid", auid),
                        Pair("like", like),
                        Pair("love", love)
                    )
                ).setClazz(ArticleInfoForNormalFragment::class.java).start()
        }
    }

    private var mCurrentArticle: ArticleItem? = null

    private val aid by lazy {
        arguments?.getInt("aid", 0)
    }

    private val auid by lazy {
        arguments?.getInt("auid", 0)
    }

    private var mCurrentMenuItem: MenuItem? = null

    private var menu : Menu? = null

    private val mHeaderView by lazy {
        View.inflate(mContext, R.layout.layout_article_info_header_view, null)
    }

    private val mHeaderWebView by lazy {
        mHeaderView.findViewById<WebView>(R.id.mWVArticleInfoForNormal)
    }

    private var mCurrentArticleItem: ArticleCommentItem? = null

    private var admireDialog : ArticleAdmireDialogFragment? = null

    private val updateArticleSubscription by lazy {
        RxBus.getDefault().toObservable(String::class.java).subscribe {
            if (it == UPDATE_ARTICLE_FLAG){
                RxBus.getDefault().post(mCurrentArticle)
                mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DETAIL_ACT).addParam("aid", aid))
            }
        }
    }

    private var focusTextView:CheckedTextView? = null

    private val shareDialog by lazy {  ArticleShareDialogFragment() }

    private val weiboShareHandler by lazy {
        WbShareHandler(mContext as Activity)
    }

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(mContext,BaseConfig.WX_APP_ID,true)
    }

    override fun getLayoutId() = R.layout.fragment_article_info_for_normal_layout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun createAdapter() = object : BaseQuickAdapter<ArticleCommentItem, BaseViewHolder>(R.layout.adapter_article_comment_info_item_layout,mArrayList) {
        override fun convert(helper: BaseViewHolder?, item: ArticleCommentItem?) {
            GlideManager.loadFaceCircleImage(
                mContext,
                item?.comment_userData?.member_pic,
                helper?.getView(R.id.mIvArticleCommentInfoItemUserFace)
            )
            helper?.setText(R.id.mTvArticleCommentInfoItemUserNickName, item?.comment_userData?.member_nickname)
            helper?.setText(R.id.mTvArticleCommentInfoItemDes, "${item?.comment_f}楼 . ${item?.comment_times}")
            helper?.setText(R.id.mTvArticleCommentInfoItemContent, item?.comment_content)
            val replyContainer = helper?.getView<CommentLinearLayout>(R.id.mCLLArticleCommentInfoItemReplyContainer)
            replyContainer?.setOnSeeMore {
                ArticleWebViewFragment.startArticleWebViewFragment(
                    mContext,
                    "评论详情",
                    "comment-details.html?theme=${item?.comment_id ?: 0}}",
                    ArticleType.OTHER.type
                )
            }
            helper?.addOnClickListener(R.id.mIvArticleCommentInfoItemComment)
            helper?.addOnClickListener(R.id.mIvArticleCommentInfoItemPraise)
            replyContainer?.setData(item?.comment_reply,item?.comment_replyNum ?: 0)
            if (item!!.comment_like == 1) {
                helper?.setImageResource(R.id.mIvArticleCommentInfoItemPraise, R.drawable.img_praise_icon_2)
            } else {
                helper?.setImageResource(R.id.mIvArticleCommentInfoItemPraise, R.drawable.img_praise_icon_1)
            }
        }
    }

    override fun initView(view: View?) {
        super.initView(view)
        updateArticleSubscription
        setRefreshEnable(false)
        mBaseAdapter.setHeaderAndEmpty(true)
        mHeaderWebView.setNormalSetting()
        mBaseAdapter.addHeaderView(mHeaderView)
        mHeaderView.findViewById<LinearLayout>(R.id.mLlArticleInfoForAuthInfo).doClickWithUserStatusStart("") {
            PersonalIndexFragment.startPersonalIndexFragment(mContext,mCurrentArticle?.articles_userData?.member_id ?: -1)
        }
        mTvArticleInfoForNormalBottomComment.doClickWithUserStatusStart("") {
            comment(0x2, -1, -1, mCurrentArticle?.articles_userData?.member_nickname, ARTICLE_COMMENT_DO_ACT, 1)
        }
        mTvArticleInfoForNormalBottomActionComment.setOnClickListener {
            mRecyclerView.smoothScrollToPosition(1)
        }
        mTvArticleInfoForNormalBottomActionAdmire.doClickWithUserStatusStart("") {
            if (mCurrentArticle!=null) {
                if (mCurrentArticle?.articles_uid == UserInfo.user_id){
                    toast("不可以给自己赞赏")
                    return@doClickWithUserStatusStart
                }
                admireDialog = ArticleAdmireDialogFragment()
                admireDialog?.setCallBack { content, price ->
                    mPresent.getDataByPost(0x10, getBaseParamsWithModAndAct(ARTICLE_MODULE,ARTICLE_ADMIRE_ACT)
                        .addParam("cuid",mCurrentArticle?.articles_userData?.member_id)
                        .addParam("content",content)
                        .addParam("theme",mCurrentArticle?.articles_id)
                        .addParam("price",price))
                    admireDialog?.dismiss()
                }
                admireDialog?.setFacePath(mCurrentArticle?.articles_userData?.member_pic ?: "")
                admireDialog?.show(childFragmentManager, "admire_dialog")
            }
        }
        mTvArticleInfoForNormalBottomActionShare.setOnClickListener {
            if (mCurrentArticle!=null) {
                shareDialog.setWeiBoShareHandler(weiboShareHandler)
                shareDialog.setWxApi(wxApi)
                shareDialog.setShareFriend {
                    MyFriendListFragment.startFriendListFragment(context!!,mCurrentArticle?.articles_title ?: "",mCurrentArticle?.articles_desc ?: "",mCurrentArticle?.articles_id ?: 0)
                }
                shareDialog.setShareArticle(ArticleShareItem(mCurrentArticle?.articles_title,mCurrentArticle?.articles_desc,"","${mCurrentArticle?.articles_shareUrl}?theme=${mCurrentArticle?.articles_id ?: 0}"))
                shareDialog.setCreateImage {
                    val shortImage = Bitmap.createBitmap(
                        mContext.getScreen().first,
                        mHeaderWebView.measuredHeight,
                        Bitmap.Config.RGB_565
                    )
                    Observable.just(shortImage).map {
                        val canvas = Canvas(it)   // 画布的宽高和屏幕的宽高保持一致
                        mHeaderWebView.draw(canvas)
                        val dir = File(BaseConfig.IMAGE_PATH)
                        dir.mkdirs()
                        val shareBitmapFile = File(dir, "${System.currentTimeMillis()}.jpg")
                        if (!shareBitmapFile.exists()) {
                            shareBitmapFile.createNewFile()
                        }
                        ImageFactory.compressAndGenImage(shortImage, shareBitmapFile.absolutePath, 1024 * 2)
                        shareBitmapFile
                    }.subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            getFastProgressDialog("加载中……")
                        }.observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            progressDialog?.dismiss()
                            ImagePosterShareFragment.startImagePosterShareFragment(mContext,it.absolutePath)
                        }
                }
                shareDialog.show(childFragmentManager, "share_dialog")
            }
        }
        mTvArticleInfoForNormalBottomActionLove.doClickWithUserStatusStart("") {
            mPresent.getDataByPost(
                0x4,
                getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_LOVE_ACT).addParam("aid", aid)
            )
        }
        mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DETAIL_ACT).addParam("aid", aid))
    }

    override fun onRequestStart(requestID: Int) {
        when (requestID) {
            0x1 -> getFastProgressDialog("正在加载……")
            0x2 -> getFastProgressDialog("正在评论……")
            0x4 -> getFastProgressDialog("操作中……")
            0x5 -> getFastProgressDialog("收藏中……")
            0x7 -> getFastProgressDialog("删除中……")
            0x8 -> getFastProgressDialog("设置中……")
            0x9 -> getFastProgressDialog("投稿中……")
            0x10 -> getFastProgressDialog("赞赏中……")
            0x11 -> getFastProgressDialog("加载中……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        when (requestID) {
            0x1 -> {
                val check = checkResultCode(result)
                if (check != null && check.code == SUCCESS_CODE) {
                    mCurrentArticle = Gson().fromJson(
                        (check.obj as JSONObject).optJSONObject(RESULT_OBJECT).toString(),
                        ArticleItem::class.java
                    )
                    mHeaderWebView.post {
                        val count = menu?.size() ?: 0
                        if (UserInfo.user_id == mCurrentArticle?.articles_uid) {
                            when (ArticleType.getTypeNameById(mCurrentArticle?.articles_status ?: 0)) {
                                ArticleType.PUBLIC_ARTICLE -> {
                                    (0 until count).forEach {
                                        val item = menu?.getItem(it)
                                        item?.isVisible = true
                                    }
                                }
                                ArticleType.POST_ARTICLE,ArticleType.COLLECTION_ARTICLE -> {
                                    (0 until count).forEach {
                                        val item = menu?.getItem(it)
                                        if (item?.itemId == R.id.mMenuArticleCollection || item?.itemId == R.id.mMenuArticleShare){
                                            item.isVisible = true
                                        }else{
                                            item?.isVisible = false
                                        }
                                    }
                                }
                            }
                        }else{
                            (0 until count).forEach {
                                menu?.getItem(it)?.isVisible = it == 0 || it == 1
                            }
                        }
                        mHeaderWebView.loadWrapperData(mCurrentArticle?.articles_content)
                        mHeaderWebView.webViewClient = object : WebViewClient() {

                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                if (url!=null){
                                    if (url.startsWith("http://") || url.startsWith("https://")){
                                        NetWebViewFragment.startNetWebViewFragment(mContext, url ?: "")
                                        return true
                                    }
                                }
                                return super.shouldOverrideUrlLoading(view,url)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                if (request?.url!=null && request.url!=null){
                                    if (request.url!!.toString().startsWith("http://") || request.url!!.toString().startsWith("https://")){
                                        NetWebViewFragment.startNetWebViewFragment(mContext,  request.url!!.toString())
                                        return true
                                    }
                                }
                                return super.shouldOverrideUrlLoading(view,request)
                            }
                        }
                        mHeaderView.findViewById<TextView>(R.id.mTvArticleInfoTitle)
                            .text = mCurrentArticle?.articles_title ?: ""
                        mHeaderView.findViewById<TextView>(R.id.mTvArticleInfoForAuthInfoNickName).text =
                                mCurrentArticle?.articles_userData?.member_nickname
                        mHeaderView.findViewById<TextView>(R.id.mTvArticleInfoType)
                            .text = mCurrentArticle?.articles_tags ?: ""
                        mHeaderView.findViewById<TextView>(R.id.mTvArticleInfoDes).text =
                                "${mCurrentArticle?.articles_times} . 字数${mCurrentArticle?.articles_numcount} . 阅读${mCurrentArticle?.articles_read}"
                        GlideManager.loadFaceCircleImage(
                            mContext,
                            mCurrentArticle?.articles_userData?.member_pic,
                            mHeaderView.findViewById(R.id.mIvArticleInfoForAuthInfoFace)
                        )
                        mTvArticleInfoForNormalBottomActionComment.text = "评论 ${mCurrentArticle?.articles_commentCount}"
                        mTvArticleInfoForNormalBottomActionLove.text = "喜欢 ${mCurrentArticle?.articles_loveCount}"
                        if (mCurrentArticle?.articles_love == 0) { // 不喜欢
                            mTvArticleInfoForNormalBottomActionLove.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                R.drawable.img_like_1_icon,
                                0,
                                0
                            )
                        } else {
                            mTvArticleInfoForNormalBottomActionLove.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                R.drawable.img_like_icon,
                                0,
                                0
                            )
                        }
                        if (mCurrentArticle?.articles_collect == 0) {
                            mCurrentMenuItem?.setIcon(R.drawable.img_collection_icon1)
                        } else {
                            mCurrentMenuItem?.setIcon(R.drawable.img_collection_icon2)
                        }
                        focusTextView = mHeaderView.findViewById(R.id.mCtvArticleInfoForAuthInfoFocus)
                        if (UserInfo.user_id == mCurrentArticle?.articles_uid) {
                            focusTextView?.visibility = View.GONE
                        } else {
                            focusTextView?.visibility = View.VISIBLE
                            if (mCurrentArticle?.articles_like == 0) {
                                focusTextView?.text = "+ 关注"
                                focusTextView?.isChecked = false
                            } else {
                                focusTextView?.text = "✓ 已关注"
                                focusTextView?.isChecked = true
                            }
                        }
                        focusTextView?.doClickWithUserStatusStart("") {
                            mPresent.getDataByPost(
                                0x11,
                                getBaseParamsWithModAndAct(MEMBER_MODULE, MY_LIKE_DO_ACT).addParam("reuid", mCurrentArticle?.articles_userData?.member_id)
                            )
                        }
                    }
                }
            }
            0x0 -> processList(result as String, ArticleCommentItem::class.java)
            0x2 -> { //评论
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        val comment = Gson().fromJson(
                            (check.obj as JSONObject).optJSONObject("result").toString(),
                            ArticleCommentItem::class.java
                        )
                        mCurrentArticle?.articles_commentCount = (mCurrentArticle?.articles_commentCount ?: 0 ) + 1
                        mTvArticleInfoForNormalBottomActionComment.text = "评论 ${mCurrentArticle?.articles_commentCount}"
                        mBaseAdapter.addData(0, comment)
                        mBaseAdapter.loadMoreComplete()
                        mBaseAdapter.disableLoadMoreIfNotFullPage()
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x3 -> { //添加评论
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        val comment = Gson().fromJson(
                            (check.obj as JSONObject).optJSONObject("result").toString(),
                            ArticleCommentReply::class.java
                        )
                        mCurrentArticleItem?.comment_reply?.add(comment)
                        mBaseAdapter.notifyItemChanged(mArrayList.indexOf(mCurrentArticleItem) + 1)
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x4 -> { //喜欢
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        UserInfo.reloadUserInfo()
                        if (mCurrentArticle?.articles_love == 0) {
                            mCurrentArticle?.articles_love = 1
                            val loveCount = (mCurrentArticle?.articles_loveCount ?:0) + 1
                            mCurrentArticle?.articles_loveCount = loveCount
                            mTvArticleInfoForNormalBottomActionLove.text = "喜欢 $loveCount"
                            toast("喜欢成功")
                            mTvArticleInfoForNormalBottomActionLove.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                R.drawable.img_like_icon,
                                0,
                                0
                            )
                        } else {
                            mCurrentArticle?.articles_love = 0
                            var loveCount = (mCurrentArticle?.articles_loveCount ?:0) - 1
                            loveCount = Math.max(loveCount,0)
                            mCurrentArticle?.articles_loveCount = loveCount
                            mTvArticleInfoForNormalBottomActionLove.text = "喜欢 $loveCount"
                            toast("取消喜欢成功")
                            mTvArticleInfoForNormalBottomActionLove.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                R.drawable.img_like_1_icon,
                                0,
                                0
                            )
                        }
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x5 -> {
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        UserInfo.reloadUserInfo()
                        if (mCurrentArticle?.articles_collect == 0) {
                            mCurrentArticle?.articles_collect = 1
                            toast("收藏成功")
                            mCurrentMenuItem?.setIcon(R.drawable.img_collection_icon2)
                        } else {
                            mCurrentArticle?.articles_collect = 0
                            toast("取消收藏成功")
                            mCurrentMenuItem?.setIcon(R.drawable.img_collection_icon1)
                        }
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x6 -> { //评论点赞
            }
            0x7 -> { //放入回收站
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        toast("文章删除成功")
                        RxBus.getDefault().post(mCurrentArticle)
                        finish()
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x8 -> { //设置为私密
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        toast("文章设置私密成功")
                        RxBus.getDefault().post(mCurrentArticle)
                        finish()
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x9 -> { //投稿
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        toast("文章投稿成功")
                        RxBus.getDefault().post(mCurrentArticle)
                        finish()
                    } else {
                        toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x10-> { //赞赏
                val check = checkResultCode(result)
                if (check != null) {
                    when {
                        check.code == SUCCESS_CODE -> toast("赞赏成功")
                        check.code == "402" -> {
                            toast("余额不足，请充值")
                            WebViewContainerActivity.startWebViewContainerActivity(mContext, "wallet.html")
                        }
                        else -> toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }
            }
            0x11-> { //关注
                val check = checkResultCode(result)
                if (check != null) {
                    toast((check.obj as JSONObject).optString(MSG_FLAG))
                    if (check.code == SUCCESS_CODE) {
                        RxBus.getDefault().post(FocusFragment.UPDATE_FOCUS_FLAG)
                        UserInfo.reloadUserInfo()
                        if (mCurrentArticle?.articles_like == 1){//
                            mCurrentArticle?.articles_like = 0
                            focusTextView?.text = "+ 关注"
                            focusTextView?.isChecked = false
                        }else{
                            mCurrentArticle?.articles_like = 1
                            focusTextView?.text = "✓ 已关注"
                            focusTextView?.isChecked = true
                        }
                    } else {
                        toast("关注失败，请重试……")
                    }
                }
            }
        }
    }

    override fun getEmptyMessage(): String {
        return "暂无评论"
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(
            0x0, getBaseParamsWithPage(ARTICLE_MODULE, ARTICLE_COMMENT_ACT, currentPage)
                .addParam("theme", aid)
        )
    }

    override fun onLoadMore() {
        super.onLoadMore()
        mPresent.getDataByPost(
            0x0, getBaseParamsWithPage(ARTICLE_MODULE, ARTICLE_COMMENT_ACT, currentPage)
                .addParam("theme", aid)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.article_normal_menu, menu)
        this.menu = menu
        if (menu != null) {
            if (menu.javaClass == MenuBuilder::class.java) {
                val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                method.isAccessible = true
                method.invoke(menu, true)
            }
            if (UserInfo.user_id != auid) {
                (0 until menu.size()).forEach {
                    menu.getItem(it).isVisible = it == 0 || it == 1
                }
            }
            (0 until menu.size()).forEach {
                if (menu.getItem(it).itemId == R.id.mMenuArticleCollection) {
                    mCurrentMenuItem = menu.getItem(it)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mMenuArticleCollection -> {
                if(UserInfo.isLogin()) {
                    mCurrentMenuItem = item
                    mPresent.getDataByPost(
                        0x5,
                        getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_COLLECT_ACT).addParam("aid", aid)
                    )
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
            R.id.mMenuArticleShare->{
                mTvArticleInfoForNormalBottomActionShare.performClick()
            }
            R.id.mMenuArticleEdit->{
                if (UserInfo.isLogin()) {
                    ArticleEditActivity.startArticleEditActivity(
                        mContext,
                        mCurrentArticle?.articles_title ?: "",
                        mCurrentArticle?.articles_content ?: "",
                        Classify(0, "", false, ""),
                        1,
                        mCurrentArticle?.articles_id ?: 0
                    )
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
            R.id.mMenuArticlePrivate->{
                if (UserInfo.isLogin()) {
                    alert("是否要将此文章设为私密？", "是", "否") { _, _ ->
                        mPresent.getDataByPost(
                            0x8, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_STATUS_ACT)
                                .addParam("aid", aid)
                                .addParam("status", "2")
                        )
                    }
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
            R.id.mMenuArticlePost->{
                if (UserInfo.isLogin()) {
                    alert("是否要投稿？", "是", "否") { _, _ ->
                        mPresent.getDataByPost(
                            0x9, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_STATUS_ACT)
                                .addParam("aid", aid)
                                .addParam("status", "4")
                        )
                    }
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
            R.id.mMenuArticleDelete->{
                if (UserInfo.isLogin()) {
                    alert("是否要将此文章放入回收站？", "是", "否") { _, _ ->
                        mPresent.getDataByPost(
                            0x7, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DEL_STATUS_ACT)
                                .addParam("aid", aid)
                                .addParam("status", "5")
                        )
                    }
                }else{
                    LoginFragment.startLoginFragment(mContext)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMyItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemChildClick(adapter, view, position)
        if (UserInfo.isLogin()) {
            mCurrentArticleItem = mArrayList[position]
            if (view?.id == R.id.mIvArticleCommentInfoItemComment) {
                comment(
                    0x3,
                    mCurrentArticleItem?.comment_id,
                    mCurrentArticleItem?.comment_uid,
                    mArrayList[position].comment_userData?.member_nickname,
                    ARTICLE_COMMENT_REPLY_ACT,
                    2
                )
            } else if (view?.id == R.id.mIvArticleCommentInfoItemPraise) {
                mCurrentArticleItem?.comment_like = if (mCurrentArticleItem?.comment_like == 1) {
                    0
                } else {
                    1
                }
                mBaseAdapter.notifyItemChanged(position + 1)
                mPresent.getDataByPost(
                    0x6, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_COMMENT_LIKE_ACT)
                        .addParam("theme", mCurrentArticleItem?.comment_id)
                )
            }
        }else{
            LoginFragment.startLoginFragment(mContext)
        }
    }

    private fun comment(requestID: Int, reid: Int?, cuid: Int?, nickName: String?, act: String, type: Int) {
        val contentView = View.inflate(mContext, R.layout.dialog_article_comment_layout, null)
        val et_content = contentView.findViewById<EditText>(R.id.mEtArticleCommentContent)
        et_content.hint = "@$nickName"
        val bt_submit = contentView.findViewById<TextView>(R.id.mTvArticleCommentSubmit)
        val popUpWindow = PopupWindowDialog.showReplyDialog(mContext, contentView)
        bt_submit.setOnClickListener {
            if (et_content.isEmpty()) {
                toast("请输入评论内容")
                return@setOnClickListener
            }
            mContext.hideSoftInput(et_content)
            popUpWindow.dismiss()
            mPresent.getDataByPost(
                requestID, getBaseParamsWithModAndAct(ARTICLE_MODULE, act)
                    .addParam("theme", aid)
                    .addParam("cuid", cuid)
                    .addParam("reid", reid)
                    .addParam("content", et_content.getTextString())
                    .addParam("type", "$type")
            )
        }
        contentView.post { PopupWindowDialog.toggleSoft(mContext) }
    }

    override fun onMyActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        weiboShareHandler.doResultIntent(data, object : WbShareCallback {
                override fun onWbShareFail() {
                    toast("分享失败")
                }

                override fun onWbShareCancel() {
                    toast("分享取消")
                }

                override fun onWbShareSuccess() {
                    toast("分享成功")
                }
            })
    }

    override fun onDestroyView() {
        unsubscribe(updateArticleSubscription)
        super.onDestroyView()
    }

}