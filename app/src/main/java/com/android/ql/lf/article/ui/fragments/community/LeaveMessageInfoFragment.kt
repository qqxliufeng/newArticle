package com.android.ql.lf.article.ui.fragments.community

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.LeaveMessage
import com.android.ql.lf.article.ui.fragments.article.ArticleInfoForNormalFragment
import com.android.ql.lf.article.ui.fragments.mine.PersonalIndexFragment
import com.android.ql.lf.article.ui.widgets.PopupWindowDialog
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast

/**
 * Created by lf on 18.11.8.
 * @author lf on 18.11.8
 */
class LeaveMessageInfoFragment : BaseRecyclerViewFragment<LeaveMessage>() {

    companion object {
        fun startLeaveMessageInfoFragment(context: Context,cuid:Int){
            FragmentContainerActivity
                .from(context)
                .setTitle("留言详情")
                .setNeedNetWorking(true)
                .setExtraBundle(bundleOf(Pair("cuid",cuid)))
                .setClazz(LeaveMessageInfoFragment::class.java)
                .start()
        }
    }

    private val cuid by lazy {
        arguments?.getInt("cuid") ?: 0
    }

    override fun createAdapter() =
        object : BaseQuickAdapter<LeaveMessage, BaseViewHolder>(R.layout.adapter_leave_message_info_item_layout, mArrayList) {
            override fun convert(helper: BaseViewHolder?, item: LeaveMessage?) {
                helper?.setText(R.id.mTvLeaveMessageItemUserNickName,item?.leave_userData?.member_nickname)
                GlideManager.loadFaceCircleImage(mContext,item?.leave_userData?.member_pic,helper?.getView(R.id.mIvLeaveMessageItemUserFace))
                if (item?.leave_theme == 0) {
                    helper?.setTextColor(R.id.mTvLeaveMessageItemContent,mContext.resources.getColor(R.color.blackTextColor))
                    helper?.setText(R.id.mTvLeaveMessageItemContent, item.leave_content)
                }else{
                    helper?.setTextColor(R.id.mTvLeaveMessageItemContent,mContext.resources.getColor(android.R.color.holo_blue_dark))
                    helper?.setText(R.id.mTvLeaveMessageItemContent, "《${item?.leave_content}》")
                }
                helper?.setText(R.id.mTvLeaveMessageItemTime,item?.leave_times)
                helper?.addOnClickListener(R.id.mTvLeaveMessageItemReply)
                helper?.addOnClickListener(R.id.mTvLeaveMessageItemUserNickName)
                helper?.addOnClickListener(R.id.mIvLeaveMessageItemUserFace)
                helper?.addOnClickListener(R.id.mTvLeaveMessageItemContent)
            }
        }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        val itemDecoration = super.getItemDecoration() as DividerItemDecoration
        itemDecoration.setDrawable(ColorDrawable(Color.TRANSPARENT))
        return itemDecoration
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE,MY_LEAVE_DETAIL_ACT,currentPage)
            .addParam("cuid",cuid))
    }

    override fun onLoadMore() {
        super.onLoadMore()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE,MY_LEAVE_DETAIL_ACT,currentPage)
            .addParam("cuid",cuid))
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x1){
            getFastProgressDialog("正在回复……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            processList(result as String,LeaveMessage::class.java)
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check!=null){
                if (check.code == SUCCESS_CODE){
                    toast("回复发表成功")
                }else{
                    toast("回复发表失败")
                }
            }else{
                toast("回复发表失败")
            }
        }
    }

    override fun onMyItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemChildClick(adapter, view, position)
        when {
            view!!.id == R.id.mTvLeaveMessageItemReply -> {
                val contentView = View.inflate(mContext,R.layout.dialog_article_comment_layout,null)
                val popUpWindow = PopupWindowDialog.showReplyDialog(mContext,contentView)
                val submit = contentView.findViewById<TextView>(R.id.mTvArticleCommentSubmit)
                val content = contentView.findViewById<EditText>(R.id.mEtArticleCommentContent)
                submit.text = "发表回复"
                submit.setOnClickListener {
                    if (content.isEmpty()){
                        toast("请输入留言内容")
                        return@setOnClickListener
                    }
                    popUpWindow.dismiss()
                    mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MESSAGE_MODULE, LEAVE_DO_ACT)
                        .addParam("cuid",cuid)
                        .addParam("content",content.getTextString()))
                }
                contentView.post { PopupWindowDialog.toggleSoft(mContext) }
            }
            view.id == R.id.mIvLeaveMessageItemUserFace || view.id == R.id.mTvLeaveMessageItemUserNickName -> PersonalIndexFragment.startPersonalIndexFragment(mContext,mArrayList[position].leave_userData?.member_id ?: 0)
            view.id == R.id.mTvLeaveMessageItemContent ->{
                if (mArrayList[position].leave_theme != 0){
                   ArticleInfoForNormalFragment.startArticleInfoForNormal(mContext,mArrayList[position].leave_theme)
                }
            }
        }
    }
}