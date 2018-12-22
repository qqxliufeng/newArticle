package com.android.ql.lf.article.ui.fragments.mine

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.*
import com.android.ql.lf.article.ui.activity.WebViewContainerActivity
import com.android.ql.lf.article.ui.adapters.ArticleListAdapter
import com.android.ql.lf.article.ui.fragments.article.ArticleAdmireDialogFragment
import com.android.ql.lf.article.ui.fragments.article.ArticleInfoForNormalFragment
import com.android.ql.lf.article.ui.fragments.share.PersonalIndexShareDialogFragment
import com.android.ql.lf.article.ui.widgets.PopupWindowDialog
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_personal_index_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.ArrayList

class PersonalIndexFragment : BaseRecyclerViewFragment<ArticleItem>() {

    companion object {
        fun startPersonalIndexFragment(context: Context,pid:Int) {
            FragmentContainerActivity
                .from(context)
                .setClazz(PersonalIndexFragment::class.java)
                .setNeedNetWorking(true)
                .setHiddenToolBar(true)
                .setExtraBundle(bundleOf(Pair("pid",pid)))
                .start()
        }
    }

    private val pid by lazy {
        arguments?.getInt("pid") ?: -1
    }

    private var tempItem:ArticleItem? = null

    private var nickName:String? = null
    private var facePath:String? = null

    private var sharePath:String? = null

    private val updateArticleListSubscription by lazy {
        RxBus.getDefault().toObservable(ArticleItem::class.java).subscribe {
            if (tempItem != null && tempItem!!.articles_id == it.articles_id) {
                onPostRefresh()
            }
        }
    }

    private val shareFragment by lazy {
        PersonalIndexShareDialogFragment()
    }

    private val weiboShareHandler by lazy {
        WbShareHandler(mContext as Activity)
    }

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(mContext, BaseConfig.WX_APP_ID,true)
    }

    private val shareItem by lazy {
        PersonalShareItem()
    }


    override fun createAdapter() = ArticleListAdapter(mArrayList)

    override fun getLayoutId() = R.layout.fragment_personal_index_layout

    private var focusStatus = 0

    override fun initView(view: View?) {
        (mContext as FragmentContainerActivity).statusBarColor = Color.TRANSPARENT
        super.initView(view)
        updateArticleListSubscription
        setRefreshEnable(false)
        mIvBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
        mIvBack.setColorFilter(Color.WHITE)
        mAlPersonalIndex.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val alpha = (Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()) * 255).toInt()
            val bgColor = Color.argb(alpha, 255, 255, 255)
            mTlPersonalIndexBar.setBackgroundColor(bgColor)
            when {
                verticalOffset == 0 -> {
                    mIvBack.setColorFilter(Color.WHITE)
                    mIvShare.setColorFilter(Color.WHITE)
                    mCTLPersonalIndex.title = ""
                }
                Math.abs(verticalOffset) == appBarLayout.totalScrollRange -> {
                    mIvBack.setColorFilter(Color.BLACK)
                    mIvShare.setColorFilter(Color.BLACK)
                    mCTLPersonalIndex.title = nickName ?: ""
                }
                else -> {
                    mCTLPersonalIndex.title = ""
                }
            }
        }
        mIvBack.setOnClickListener { finish() }
        mLlPersonalIndexLeaveMessage.setOnClickListener {
            val contentView = View.inflate(mContext,R.layout.dialog_article_comment_layout,null)
            val popUpWindow = PopupWindowDialog.showReplyDialog(mContext,contentView)
            val submit = contentView.findViewById<TextView>(R.id.mTvArticleCommentSubmit)
            val content = contentView.findViewById<EditText>(R.id.mEtArticleCommentContent)
            submit.text = "发表留言"
            submit.setOnClickListener {
                if (content.isEmpty()){
                    toast("请输入留言内容")
                    return@setOnClickListener
                }
                popUpWindow.dismiss()
                mPresent.getDataByPost(0x3, getBaseParamsWithModAndAct(MESSAGE_MODULE,LEAVE_DO_ACT)
                    .addParam("cuid",pid)
                    .addParam("content",content.getTextString()))
            }
            contentView.post { PopupWindowDialog.toggleSoft(mContext) }
        }
        mTvPersonalIndexPraise.setOnClickListener {
            val admireDialog = ArticleAdmireDialogFragment()
            admireDialog.setCallBack{content, price ->
                mPresent.getDataByPost(0x4, getBaseParamsWithModAndAct(ARTICLE_MODULE,ARTICLE_ADMIRE_ACT)
                    .addParam("cuid",pid)
                    .addParam("content",content)
                    .addParam("price",price))
            }
            admireDialog.setFacePath(facePath ?: "")
            admireDialog.show(childFragmentManager,"admire_dialog")
        }
        if (pid!=-1){
            if (UserInfo.user_id == pid){
                UserInfoLiveData.observe(this, Observer<UserInfo> {
                    if (it!!.isLogin()) {
                        GlideManager.loadFaceCircleImage(mContext,UserInfo.user_pic,mIvPersonalIndexUserFace)
                        mTvPersonalIndexUserNickName.text = UserInfo.user_nickname
                        mTvPersonalIndexUserDes.text = UserInfo.user_signature
                    }
                })
                mLlPersonalIndexCurrentUserContainer.visibility = View.VISIBLE
                mLlPersonalIndexFocus.visibility = View.GONE
                mTvPersonalEditBgImage.visibility = View.VISIBLE
                mTvPersonalIndexEditInfo.setOnClickListener {
                    PersonalEditFragment.startPersonalEditFragment(mContext)
                }
                mTvPersonalEditBgImage.setOnClickListener {
                    openImageChoose(MimeType.ofImage(),1)
                }
            }else{
                mLlPersonalIndexCurrentUserContainer.visibility = View.GONE
                mLlPersonalIndexFocus.visibility = View.VISIBLE
                mTvPersonalEditBgImage.visibility = View.GONE
            }
        }
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MEMBER_MODULE, MY_MAIN_ACT,currentPage).addParam("reuid",pid))
    }

    override fun onLoadMore() {
        super.onLoadMore()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MEMBER_MODULE, MY_MAIN_ACT,currentPage).addParam("reuid",pid))
    }

    override fun getEmptyMessage(): String {
        return "暂无文章哦~"
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        when (requestID) {
            0x2 -> getFastProgressDialog("操作中……")
            0x3 -> getFastProgressDialog("正在发表留言……")
            0x4 -> getFastProgressDialog("正在发表留言……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        when (requestID) {
            0x0 -> {
                processList(result as String,ArticleItem::class.java)
                val check = checkResultCode(result)
                if (check!=null && currentPage == 0){
                    val json = (check.obj as JSONObject).optJSONObject("arr")
                    if (json!=null){
                        sharePath = "${json.optString("member_shareUrl")}?uid=${UserInfo.user_id}&reuid=$pid"
                        facePath = json.optString("member_pic")
                        GlideManager.loadFaceCircleImage(mContext,facePath,mIvPersonalIndexUserFace)
                        nickName = json.optString("member_nickname")
                        mTvPersonalIndexUserNickName.text = nickName
                        mTvPersonalIndexUserDes.text = if (TextUtils.isEmpty(json.optString("member_signature"))) { "暂无简介" }else{ json.optString("member_signature") }
                        mTvPersonalIndexFocusCount.text = "${json.optString("member_likeCount")}关注"
                        mTvPersonalIndexFansCount.text = "${json.optString("member_fanCount")}粉丝"
                        mTvPersonalIndexArticleCount.text = "文章（${json.optString("member_articleCount")}）"
                        mTvPersonalIndexNumAndLove.text = "写了${json.optString("member_fontCount")}字，获得了${json.optString("member_loveCount")}个喜欢"
                        mTvPersonalIndexAddress.text = if (TextUtils.isEmpty(json.optString("member_address")) || json.optString("member_address") == "null"){ "北京" } else { json.optString("member_address") }
                        val age = json.optString("member_age")
                        if (TextUtils.isEmpty(age) || age == "null") {
                            mTvPersonalIndexAge.text = "00"
                        }else{
                            mTvPersonalIndexAge.text = "${json.optString("member_age")}"
                        }
                        focusStatus = json.optInt("member_likeStatus")
                        val cover = json.optString("member_cover")
                        if (!TextUtils.isEmpty(cover)) {
                            GlideManager.loadImage(mContext,cover,mIvPersonalEditBgImage)
                        }
                        if (focusStatus == 0){
                            mTvPersonalIndexFocus.text = "+ 关注"
                        }else{
                            mTvPersonalIndexFocus.text = "✓ 已关注"
                        }
                        mTvPersonalIndexFocus.setOnClickListener {
                            mPresent.getDataByPost(
                                0x2,
                                getBaseParamsWithModAndAct(MEMBER_MODULE, MY_LIKE_DO_ACT).addParam("reuid", pid)
                            )
                        }
                        mIvShare.setOnClickListener {
                            shareFragment.setWxApi(wxApi)
                            shareFragment.setWeiBoShareHandler(weiboShareHandler)
                            shareItem.title = "推荐作者：$nickName"
                            shareItem.content = "推荐作者：$nickName"
                            shareItem.shareImagePath = facePath
                            shareItem.url = sharePath
                            shareFragment.setShareArticle(shareItem)
                            shareFragment.show(childFragmentManager,"share_dialog")
                        }
                    }
                }
            }
            0x1 -> {
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("图片保存成功")
                        val cover = (check.obj as JSONObject).optString("result")
                        if (!TextUtils.isEmpty(cover)) {
                            UserInfo.user_cover = cover
                            GlideManager.loadImage(mContext,cover,mIvPersonalEditBgImage)
                        }
                    }
                }
            }
            0x2->{
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        UserInfo.reloadUserInfo()
                        if (focusStatus == 0){
                            focusStatus = 1
                            mTvPersonalIndexFocus.text = "✓ 已关注"
                        }else{
                            focusStatus = 0
                            mTvPersonalIndexFocus.text = "+ 关注"
                        }
                    }else{
                        toast("操作失败，请重试")
                    }
                }else{
                    toast("操作失败，请重试")
                }
            }
            0x3->{
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("留言发表成功……")
                    }else{
                        toast("留言发表失败……")
                    }
                }else{
                    toast("留言发表失败……")
                }
            }
            0x4->{
                val check = checkResultCode(result)
                if (check!=null){
                    when {
                        check.code == SUCCESS_CODE -> toast("赞赏成功")
                        check.code == "402" -> {
                            toast("余额不足，请充值")
                            WebViewContainerActivity.startWebViewContainerActivity(mContext, "wallet.html")
                        }
                        else -> toast((check.obj as JSONObject).optString(MSG_FLAG))
                    }
                }else{
                    toast("赞赏失败……")
                }
            }
        }
    }

    override fun actionTempList(tempList: ArrayList<ArticleItem>?) {
        tempList?.forEach {
            it.mType = if (it.articles_picCount > 2) ArticleListAdapter.MULTI_IMAGE_TYPE else ArticleListAdapter.SINGLE_IMAGE_TYPE
        }
    }

    override fun onMyItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemClick(adapter, view, position)
        tempItem = mArrayList[position]
        ArticleInfoForNormalFragment.startArticleInfoForNormal(
            mContext,
            tempItem?.articles_id!!,
            tempItem?.articles_uid!!,
            tempItem?.articles_like ?: 0,
            tempItem?.articles_love ?: 0
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            compressAndSaveCacheFace(Matisse.obtainPathResult(data)[0], object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                        override fun onActionStart() {
                            getFastProgressDialog("正在保存图片……")
                        }

                        override fun onActionEnd(builder: MultipartBody.Builder?) {
                            builder?.addFormDataPart("uid","${UserInfo.user_id}")
                            mPresent.uploadFile(0x1, MEMBER_MODULE, COVER_PIC_ACT, builder?.build()?.parts())
                        }

                        override fun onActionFailed() {
                        }

                    }).upload(arrayListOf(ImageBean(null, file?.absolutePath ?: "")))
                }

                override fun onError(e: Throwable?) {
                }

                override fun onStart() {
                }
            })
        }
    }

    override fun onDestroyView() {
        unsubscribe(updateArticleListSubscription)
        super.onDestroyView()
    }

}