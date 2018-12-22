package com.android.ql.lf.article.ui.fragments.article

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleItem
import com.android.ql.lf.article.ui.fragments.other.NetWebViewFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.RxBus
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_article_info_for_trash_layout.*
import kotlinx.android.synthetic.main.layout_article_info_auth_top_view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class ArticleInfoForTrashFragment : BaseNetWorkingFragment() {

    companion object {
        fun startArticleInfoForTrashFragment(context: Context, aid: Int, auid: Int = 0){
            FragmentContainerActivity.from(context).setTitle("文章详情").setNeedNetWorking(true)
                .setExtraBundle(
                    bundleOf(
                        Pair("aid", aid),
                        Pair("auid", auid)
                    )
                ).setClazz(ArticleInfoForTrashFragment::class.java).start()
        }
    }

    private var mCurrentArticle:ArticleItem? = null

    private val aid by lazy {
        arguments?.getInt("aid") ?: 0
    }

    override fun getLayoutId() = R.layout.fragment_article_info_for_trash_layout

    @SuppressLint("JavascriptInterface","")
    override fun initView(view: View?) {
        mCtvArticleInfoForAuthInfoFocus.visibility = View.GONE
        mTrashWebView.setNormalSetting()
        mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_DETAIL_ACT).addParam("aid", aid))
    }

    override fun onRequestStart(requestID: Int) {
        when (requestID) {
            0x0 -> getFastProgressDialog("正在删除文章……")
            0x2 -> getFastProgressDialog("正在还原文章……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        when (requestID) {
            0x0 -> {
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("文章删除成功")
                        RxBus.getDefault().post(ArticleItem())
                        finish()
                    }else{
                        toast("文章删除失败，请重试……")
                    }
                }else{
                    toast("文章删除失败，请重试……")
                }
            }
            0x2 -> {
                val check = checkResultCode(result)
                if (check!=null){
                    if (check.code == SUCCESS_CODE){
                        toast("文章还原成功")
                        RxBus.getDefault().post(ArticleItem())
                        finish()
                    }else{
                        toast("文章还原失败，请重试……")
                    }
                }else{
                    toast("文章还原失败，请重试……")
                }
            }
            0x1->{
                val check = checkResultCode(result)
                if (check != null && check.code == SUCCESS_CODE) {
                    mCurrentArticle = Gson().fromJson(
                        (check.obj as JSONObject).optJSONObject(RESULT_OBJECT).toString(),
                        ArticleItem::class.java
                    )
                    mTrashWebView.post {
                        mTrashWebView.loadWrapperData(mCurrentArticle?.articles_content)
                        mTrashWebView.webViewClient = object : WebViewClient() {
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
                        mLlArticleInfoForTrashDelete.setOnClickListener {
                            alert("是否要彻底删除文章？","彻底删除","暂不删除"){_,_->
                                mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE,ARTICLE_DEL_ACT).addParam("theme",aid))
                            }
                        }
                        mLlArticleInfoForTrashRecovery.setOnClickListener {
                            alert("是否要还原文章？","还原","暂不还原"){_,_->
                                mPresent.getDataByPost(0x2, getBaseParamsWithModAndAct(ARTICLE_MODULE,ARTICLE_STATUS_ACT).addParam("status","2").addParam("aid",aid))
                            }
                        }
                    }
                }
            }
        }
    }
}