package com.android.ql.lf.article.ui.fragments.article

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleItem
import com.android.ql.lf.article.data.ArticleShareItem
import com.android.ql.lf.article.ui.activity.ArticleEditActivity
import com.android.ql.lf.article.ui.fragments.mine.IdentityAuthFragment
import com.android.ql.lf.article.ui.fragments.mine.IdentityAuthUpdateFragment
import com.android.ql.lf.article.ui.fragments.mine.MyFriendListFragment
import com.android.ql.lf.article.ui.fragments.other.NetWebViewFragment
import com.android.ql.lf.article.ui.fragments.share.ArticleShareDialogFragment
import com.android.ql.lf.article.ui.fragments.share.ImagePosterShareFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.*
import com.google.gson.Gson
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.fragment_article_info_for_display_layout.*
import kotlinx.android.synthetic.main.layout_article_info_auth_top_view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class ArticleInfoDisplayFragment : BaseNetWorkingFragment() {

    companion object {
        fun startArticleInfoDisplayFragment(context: Context, aid: Int, auid: Int = 0) {
            FragmentContainerActivity.from(context).setTitle("文章详情").setNeedNetWorking(true)
                .setExtraBundle(
                    bundleOf(
                        Pair("aid", aid),
                        Pair("auid", auid)
                    )
                ).setClazz(ArticleInfoDisplayFragment::class.java).start()
        }
    }

    private var mCurrentArticle: ArticleItem? = null

    private val shareDialog by lazy {  ArticleShareDialogFragment() }


    private val aid by lazy {
        arguments?.getInt("aid", 0)
    }

    private val weiboShareHandler by lazy {
        WbShareHandler(mContext as Activity)
    }

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(mContext,BaseConfig.WX_APP_ID,true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    private val updateArticleSubscription by lazy {
        RxBus.getDefault().toObservable(String::class.java).subscribe {
            if (it == ArticleInfoForNormalFragment.UPDATE_ARTICLE_FLAG){
                RxBus.getDefault().post(mCurrentArticle)
                mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DETAIL_ACT).addParam("aid", aid))
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_article_info_for_display_layout

    override fun initView(view: View?) {
        updateArticleSubscription
        mHeaderWebView.setNormalSetting()
        mCtvArticleInfoForAuthInfoFocus.visibility = View.GONE
        mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DETAIL_ACT).addParam("aid", aid))
    }

    override fun onRequestStart(requestID: Int) {
        when (requestID) {
            0x1 -> getFastProgressDialog("正在加载……")
            0x0 -> getFastProgressDialog("正在公开发布……")
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
                        mHeaderWebView.loadWrapperData(mCurrentArticle?.articles_content)
                        mHeaderWebView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                NetWebViewFragment.startNetWebViewFragment(mContext, url ?: "")
                                return true
                            }
                        }
                        mTvArticleInfoTitle.text = mCurrentArticle?.articles_title ?: ""
                        mTvArticleInfoForAuthInfoNickName.text = mCurrentArticle?.articles_userData?.member_nickname
                        mTvArticleInfoType.text = mCurrentArticle?.articles_tags ?: ""
                        mTvArticleInfoDes.text = "${mCurrentArticle?.articles_times} . 字数${mCurrentArticle?.articles_numcount} . 阅读${mCurrentArticle?.articles_read}"
                        GlideManager.loadFaceCircleImage(
                            mContext,
                            mCurrentArticle?.articles_userData?.member_pic,
                            mIvArticleInfoForAuthInfoFace)
                        mLlArticleInfoForDisplayBottomEdit.setOnClickListener {
                            ArticleEditActivity.startArticleEditActivity(mContext,
                                mCurrentArticle?.articles_title?:"",
                                mCurrentArticle?.articles_content?:"",
                                Classify(0,"",false,""),
                                1,
                                mCurrentArticle?.articles_id ?: 0)
                        }
                        mLlArticleInfoForDisplayBottomShare.setOnClickListener {
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
                                        ImageFactory.compressAndGenImage(shortImage, shareBitmapFile.absolutePath, 200)
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
                    }
                }
            }
            0x0 -> {
                val check = checkResultCode(result)
                if (check != null) {
                    when {
                        check.code == SUCCESS_CODE -> {
                            toast("公开发布成功")
                            RxBus.getDefault().post(ArticleItem())
                            finish()
                        }
                        check.code == "400" -> {
                            toast("请先进行身份认证")
                            IdentityAuthFragment.startIdentityAuthFragment(mContext)
                        }
                        check.code == "500" -> {
                            toast("当前身份认证信息正信审核中……")
                            IdentityAuthUpdateFragment.startIdentityAuthUpdateFragment(mContext)
                        }
                    }
                }else{
                    toast("发布失败……")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.article_display_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mMenuArticlePublic->{
                alert("是否要公开发布？","是","否"){_,_->
                    mPresent.getDataByPost(0x0,
                        getBaseParamsWithModAndAct(ARTICLE_MODULE,ARTICLE_STATUS_ACT)
                            .addParam("status","1")
                            .addParam("aid",aid))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        unsubscribe(updateArticleSubscription)
        super.onDestroyView()
    }

}