package com.android.ql.lf.article.ui.fragments.bottom

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.*
import com.android.ql.lf.article.ui.activity.MainActivity
import com.android.ql.lf.article.ui.fragments.community.LeaveMessageInfoFragment
import com.android.ql.lf.article.ui.fragments.other.ArticleWebViewFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class MessageFragment : BaseRecyclerViewFragment<LeaveMessage>() {

    private val headerView by lazy {
        View.inflate(mContext, R.layout.top_message_bottom_layout, null)
    }

    private val mCollection by lazy {
        headerView.findViewById<FrameLayout>(R.id.mTvBottomMessageCollection)
    }

    private val mComment by lazy {
        headerView.findViewById<FrameLayout>(R.id.mTvBottomMessageComment)
    }

    private val mFocus by lazy {
        headerView.findViewById<FrameLayout>(R.id.mTvBottomMessageFocus)
    }

    private val mFocusCount by lazy {
        headerView.findViewById<TextView>(R.id.mTvBottomMessageFocusCount)
    }

    private val mCollectionCount by lazy {
        headerView.findViewById<TextView>(R.id.mTvBottomMessageCollectionCount)
    }

    private val mCommentCount by lazy {
        headerView.findViewById<TextView>(R.id.mTvBottomMessageCommentCount)
    }

    private val mMessageSwitch by lazy {
        headerView.findViewById<Switch>(R.id.mSwitchMessageBottom)
    }

    override fun getLayoutId() = R.layout.fragment_message_layout

    override fun createAdapter() =
        object :
            BaseQuickAdapter<LeaveMessage, BaseViewHolder>(R.layout.adapter_message_bottom_item_layout, mArrayList) {
            override fun convert(helper: BaseViewHolder?, item: LeaveMessage?) {
                GlideManager.loadFaceCircleImage(
                    mContext,
                    item?.leave_userData?.member_pic,
                    helper?.getView(R.id.mIvMessageBottomItemFace)
                )
                helper?.setText(R.id.mTvMessageBottomItemNickName, item?.leave_userData?.member_nickname)
                helper?.setText(R.id.mTvMessageBottomItemContent, item?.leave_content)
                helper?.setText(R.id.mTvMessageBottomItemTime, item?.leave_times)
            }
        }

    override fun initView(view: View?) {
        super.initView(view)
        UserInfoLiveData.observe(this, Observer {
            if (UserInfo.isLogin()) {
                mMessageSwitch.isChecked = UserInfo.user_push == 1
                onPostRefresh()
            }else{
                mCollectionCount.visibility = View.GONE
                mCommentCount.visibility = View.GONE
                mFocusCount.visibility = View.GONE
                mBaseAdapter.data.clear()
                mBaseAdapter.notifyDataSetChanged()
            }
        })
        mBaseAdapter.setHeaderAndEmpty(true)
        mBaseAdapter.addHeaderView(headerView)
        mFocus.doClickWithUserStatusStart("") {
            mFocusCount.visibility = View.GONE
            isCancelRedNotify()
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "关注", "attentionMessage.html", ArticleType.OTHER.type)
        }
        mCollection.doClickWithUserStatusStart("") {
            mCollectionCount.visibility = View.GONE
            isCancelRedNotify()
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "收藏和赞", "collect.html", ArticleType.OTHER.type)
        }
        mComment.doClickWithUserStatusStart("") {
            mCommentCount.visibility = View.GONE
            isCancelRedNotify()
            ArticleWebViewFragment.startArticleWebViewFragment(mContext, "评论", "comment.html", ArticleType.OTHER.type)
        }
        mMessageSwitch.doClickWithUserStatusStart("") {
            mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MESSAGE_MODULE, PUSH_ACT))
        }
    }

    private fun isCancelRedNotify(){
        if (mFocusCount.visibility == View.GONE && mCollectionCount.visibility == View.GONE && mCommentCount.visibility == View.GONE){
            UserInfo.user_allStatus = 0
            (mContext as MainActivity).setRedNotify()
        }
    }

    override fun onRefresh() {
        super.onRefresh()
        if (UserInfo.isLogin()) {
            mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE, MESSAGE_ACT, currentPage))
        }else{
            onRequestEnd(-1)
        }
    }

    override fun onLoadMore() {
        super.onLoadMore()
        if (UserInfo.isLogin()) {
            mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE, MESSAGE_ACT, currentPage))
        }
    }

    override fun getEmptyMessage(): String {
        return "暂无留言哦~"
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        val itemDecoration = super.getItemDecoration() as DividerItemDecoration
        itemDecoration.setDrawable(ColorDrawable(Color.WHITE))
        return itemDecoration
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x1) {
            getFastProgressDialog("正在设置……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            processList(result as String, LeaveMessage::class.java)
            val check = checkResultCode(result)
            if (check != null) {
                val json = (check.obj as JSONObject).optJSONObject("arr")
                val commentCount = json.optInt("comment_status")
                val focusCount = json.optInt("like_status")
                val collectionCount = json.optInt("collect_token")
                UserInfo.user_push = json.optInt("member_push")
                mMessageSwitch.isChecked = UserInfo.user_push == 1
                if (commentCount > 0) {

                    mCommentCount.visibility = View.VISIBLE
                    mCommentCount.text = "$commentCount"
                } else {
                    mCommentCount.visibility = View.GONE
                }
                if (focusCount > 0) {
                    mFocusCount.visibility = View.VISIBLE
                    mFocusCount.text = "$focusCount"
                } else {
                    mFocusCount.visibility = View.GONE
                }
                if (collectionCount > 0) {
                    mCollectionCount.visibility = View.VISIBLE
                    mCollectionCount.text = "$collectionCount"
                } else {
                    mCollectionCount.visibility = View.GONE
                }
            }
        } else if (requestID == 0x1) {
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    toast("设置成功")
                    if (UserInfo.user_push == 1) {
                        UserInfo.user_push = 2
                    } else {
                        UserInfo.user_push = 1
                    }
                } else {
                    toast("设置失败")
                }
            } else {
                toast("设置失败")
            }
            mMessageSwitch.isChecked = UserInfo.user_push != 2
        }
    }

    override fun onMyItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemClick(adapter, view, position)
        LeaveMessageInfoFragment.startLeaveMessageInfoFragment(mContext,mArrayList[position].leave_uid)
    }
}