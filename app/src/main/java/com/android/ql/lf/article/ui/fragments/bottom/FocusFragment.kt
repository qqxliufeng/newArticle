package com.android.ql.lf.article.ui.fragments.bottom

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleType
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.UserInfoLiveData
import com.android.ql.lf.article.data.isLogin
import com.android.ql.lf.article.ui.fragments.article.ArticleListItemFragment
import com.android.ql.lf.article.ui.fragments.other.ArticleWebViewFragment
import com.android.ql.lf.article.utils.ARTICLE_MODULE
import com.android.ql.lf.article.utils.ARTICLE_REUSER_ACT
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.RxBus
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_focus_layout.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class FocusFragment : BaseNetWorkingFragment() {

    companion object {
        const val UPDATE_FOCUS_FLAG = "update_focus"
    }

    private val titles = arrayListOf<FocusUserBean>()

    private var isLoad = false

    private var isUpdate = false

    private val updateFocusSubscription by lazy {
        RxBus.getDefault().toObservable(String::class.java).subscribe {
            if (it == UPDATE_FOCUS_FLAG){
                isUpdate = true
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_focus_layout

    override fun initView(view: View?) {
        updateFocusSubscription
        UserInfoLiveData.observe(this, Observer {
            if (UserInfo.isLogin() && !isLoad) {
                mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_REUSER_ACT))
            }
        })
        mIvBottomFocusSearch.setOnClickListener {
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "搜索", "search.html", ArticleType.OTHER.type)
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        getFastProgressDialog("正在加载……")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isUpdate && isVisibleToUser){
            isUpdate = false
            mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLE_REUSER_ACT))
        }
    }

    override fun onResume() {
        super.onResume()
        userVisibleHint = true
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    isLoad = true
                    val jsonArray = (check.obj as JSONObject).optJSONArray(RESULT_OBJECT)
                    if (jsonArray != null) {
                        titles.clear()
                        (0 until jsonArray.length()).forEach {
                            titles.add(Gson().fromJson(jsonArray[it].toString(), FocusUserBean::class.java))
                        }
                        val allItem = FocusUserBean()
                        allItem.like_reuid = 0
                        allItem.like_userData = FocusUserInfoBean(0,"全部")
                        titles.add(0,allItem)
                        mVpArticleList.adapter = null
                        mVpArticleList.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {

                            override fun getItem(position: Int): Fragment {
                                return ArticleListItemFragment.startArticleListItem(
                                    ArticleListItemFragment.INVALID_ID,
                                    ArticleListItemFragment.INVALID_ID,
                                    ArticleListItemFragment.INVALID_ID,
                                    2,
                                    titles[position].like_reuid
                                )
                            }

                            override fun getCount() = titles.size

                            override fun getPageTitle(position: Int) = titles[position].like_userData?.member_nickname ?: ""
                        }
                        mTlArticleList.setupWithViewPager(mVpArticleList)
                    }
                } else {
                    toast("加载失败")
                }
            } else {
                toast("加载失败")
            }
        }
    }

    override fun onDestroyView() {
        unsubscribe(updateFocusSubscription)
        super.onDestroyView()
    }
}

class FocusUserBean {
    var like_id: Int = 0
    var like_theme: Int = 0
    var like_uid: Int = 0
    var like_reuid: Int = 0
    var like_status: Int = 0
    var like_userData: FocusUserInfoBean? = null
}

class FocusUserInfoBean(val member_id: Int = 0, val member_nickname: String = "")

