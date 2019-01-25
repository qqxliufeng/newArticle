package com.android.ql.lf.article.ui.fragments.bottom

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.*
import com.android.ql.lf.article.ui.activity.AuthActivity
import com.android.ql.lf.article.ui.activity.MainActivity
import com.android.ql.lf.article.ui.activity.WebViewContainerActivity
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import com.android.ql.lf.article.ui.fragments.mine.*
import com.android.ql.lf.article.ui.fragments.other.ArticleWebViewFragment
import com.android.ql.lf.article.ui.fragments.other.SystemNotifyFragment
import com.android.ql.lf.article.ui.fragments.share.AppShareDialogFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import com.android.ql.lf.baselibaray.utils.RxBus
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.fragment_mine_layout.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject


class MineFragment : BaseNetWorkingFragment() {

    companion object {
        const val UPDATE_USER_INFO_FLAG = "update_user_info_flag"
    }

    private val shareHandler by lazy {
        WbShareHandler(mContext as Activity)
    }

    private val iWxAPI by lazy {
        WXAPIFactory.createWXAPI(mContext,BaseConfig.WX_APP_ID,true)
    }

    private val shareDialogFragment by lazy {
        AppShareDialogFragment()
    }

    private val userInfoSubscript by lazy {
        RxBus.getDefault().toObservable(String::class.java).subscribe {
            if (it == UPDATE_USER_INFO_FLAG){
               mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_ACT).addParam("uid",PreferenceUtils.getPrefInt(mContext,USER_ID_FLAG,-1)))
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_mine_layout

    override fun initView(view: View?) {
        userInfoSubscript
        UserInfoLiveData.observe(this, Observer<UserInfo> {
            if (it!!.isLogin()) {
                GlideManager.loadFaceCircleImage(mContext, UserInfo.user_pic, mIvMineFace)
                mTvMineNickName.text = UserInfo.user_nickname
                mTvMineFocusCount.text = "${UserInfo.user_likeCount}"
                mTvMinelikeCount.text = "${UserInfo.user_loveCount}"
                mTvMineFansCount.text = "${UserInfo.user_fanCount}"
                mTvMineCollectionCount.text = "${UserInfo.user_colStatus}"
                (mContext as MainActivity).onLogin()
                mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_ACT).addParam("uid",UserInfo.user_id))
            }else{
                mIvMineFace.setImageResource(R.drawable.img_glide_circle_load_default)
                mTvMineNickName.text = "登录/注册"
                mTvMineFocusCount.text = "0"
                mTvMinelikeCount.text = "0"
                mTvMineFansCount.text = "0"
                mTvMineCollectionCount.text = "0"
            }
        })
        mSrfMine.setColorSchemeColors(Color.RED,Color.BLACK,Color.BLUE)
        mSrfMine.setOnRefreshListener {
            if (UserInfo.isLogin()){
                mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_ACT).addParam("uid",UserInfo.user_id))
            }else{
                mSrfMine.isRefreshing = false
                LoginFragment.startLoginFragment(mContext)
            }
        }
        mRlMineUserInfoContainer.doClickWithUserStatusStart("") {
            PersonalIndexFragment.startPersonalIndexFragment(mContext,UserInfo.user_id)
        }
        mTvMineEditPersonalInfo.doClickWithUserStatusStart("") {
            PersonalEditFragment.startPersonalEditFragment(mContext)
        }
        mTvMineShare.setOnClickListener {
            shareDialogFragment.setWeiBoShareHandler(shareHandler)
            shareDialogFragment.setWxApi(iWxAPI)
            shareDialogFragment.setShareArticle(AppShareItem)
            shareDialogFragment.show(childFragmentManager, "app_share_dialog")
        }
        mLlMineFocusCount.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "关注", "attention.html", ArticleType.OTHER.type)
        }
        mLlMineFansCount.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "粉丝", "attentionMy.html",ArticleType.OTHER.type)
        }
        mLlMineLikeCount.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "喜欢", "alist.html",ArticleType.LOVE_ARTICLE.type)
        }
        mLlMineCollectionCount.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "收藏文章", "alist.html",ArticleType.MY_COLLECTION_ARTICLE.type)
        }
        mTvMineWallet.doClickWithUserStatusStart("") {
            WebViewContainerActivity.startWebViewContainerActivity(mContext, "wallet.html")
        }
        mTvMineTrash.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "回收站", "recovery.html",ArticleType.TRASH_ARTICLE.type)
        }
        mTvMineAuth.doClickWithUserStatusStart("")  {
            when(AuthStatus.getNameByFlag(UserInfo.user_status)){
                AuthStatus.NO_AUTH->{
                    IdentityAuthFragment.startIdentityAuthFragment(mContext)
                }
                AuthStatus.WAIT_AUTH,AuthStatus.COMPLEMENT_AUTH,AuthStatus.FAIL_AUTH->{
                    IdentityAuthUpdateFragment.startIdentityAuthUpdateFragment(mContext)
                }
            }
        }
        mTvMineCollection.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "收录文章", "contribute.html",ArticleType.COLLECTION_ARTICLE.type)
        }
        mTvMinePost.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "投稿文章", "contribute.html",ArticleType.POST_ARTICLE.type)
        }
        mTvMinePublic.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "公开文章", "contribute.html",ArticleType.PUBLIC_ARTICLE.type)
        }
        mTvMinePrivate.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "私密文章", "contribute.html",ArticleType.PRIVATE_ARTICLE.type)
        }
        mTvMineAccountSafe.doClickWithUserStatusStart("") {
            startActivity(Intent(mContext,AuthActivity::class.java))
        }
        mTvMineHistory.doClickWithUserStatusStart("") {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "浏览历史", "history.html",ArticleType.OTHER.type)
        }
        mTvMineLogout.setOnClickListener {
            if (!UserInfo.isLogin()){
                return@setOnClickListener
            }
            alert("提示", "是否要退出当前帐号？", "退出", "取消", { _, _ ->
                UserInfo.loginOut()
                (mContext as MainActivity).setRedNotify()
                (mContext as MainActivity).navigationToIndex()
            }, null)
        }
        mTvMineFeedback.doClickWithUserStatusStart("") {
            FeedBackFragment.startFeedBackFragment(mContext,1)
        }
        mTvMineProtocol.setOnClickListener {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "用户协议", "protocol.html",ArticleType.OTHER.type)
        }
        mTvMineAccountSystemNotify.setOnClickListener {
            SystemNotifyFragment.startSystemNotifyFragment(mContext)
        }
        if (!UserInfo.isLogin() && PreferenceUtils.getPrefInt(mContext,USER_ID_FLAG,-1) != -1){
            mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_ACT).addParam("uid",PreferenceUtils.getPrefInt(mContext,USER_ID_FLAG,-1)))
        }
    }

    fun onWeiboShareResult(data:Intent){
        shareHandler.doResultIntent(data, object : WbShareCallback {
            override fun onWbShareFail() {
                toast("分享失败……")
            }

            override fun onWbShareCancel() {
                toast("分享取消……")
            }

            override fun onWbShareSuccess() {
                toast("分享成功……")
            }
        })
    }

    override fun onRequestEnd(requestID: Int) {
        super.onRequestEnd(requestID)
        if (mSrfMine.isRefreshing){
            mSrfMine.isRefreshing = false
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            try {
                val check = checkResultCode(result)
                if (check != null) {
                    if (check.code == SUCCESS_CODE) {
                        val jsonObject = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                        if (UserInfo.jsonToUserInfo(jsonObject)) {
                            UserInfo.postUserInfo()
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    val jsonObject = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                    UserInfo.jsonToUserInfo(jsonObject)
                    (mContext as MainActivity).setRedNotify()
                    mTvMineFocusCount.text = "${UserInfo.user_likeCount}"
                    mTvMinelikeCount.text = "${UserInfo.user_loveCount}"
                    mTvMineFansCount.text = "${UserInfo.user_fanCount}"
                    mTvMineCollectionCount.text = "${UserInfo.user_colStatus}"
                }
            }
        }
    }

    override fun onDestroyView() {
        unsubscribe(userInfoSubscript)
        super.onDestroyView()
    }

}