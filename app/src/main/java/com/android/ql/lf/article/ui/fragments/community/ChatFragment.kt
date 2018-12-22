package com.android.ql.lf.article.ui.fragments.community

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ChatMsgItem
import com.android.ql.lf.baselibaray.utils.showSoftInput
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_chat_layout.*

/**
 * Created by lf on 18.11.7.
 * @author lf on 18.11.7
 */
class ChatFragment : BaseRecyclerViewFragment<ChatMsgItem>() {

    override fun createAdapter() =
        object : BaseMultiItemQuickAdapter<ChatMsgItem, BaseViewHolder>(mArrayList) {

            init {
                addItemType(0, R.layout.adapter_chat_msg_right_item)
                addItemType(1, R.layout.adapter_chat_msg_left_item)
            }

            override fun convert(helper: BaseViewHolder?, item: ChatMsgItem?) {
                when (item!!.itemType) {
                    ChatMsgItem.FROM_MSG_FLAG -> {

                    }
                    ChatMsgItem.SEND_MSG_FLAG -> {

                    }
                }
            }
        }

    override fun getLayoutId() = R.layout.fragment_chat_layout

    override fun initView(view: View?) {
        isNeedLoad = false
        super.initView(view)
        setRefreshEnable(false)
        onRequestEnd(-1)
        setLoadEnable(false)
        mLlChatInputEditContainer.setOnClickListener {
            mEtChatInputContent.requestFocus()
            mContext.showSoftInput(mEtChatInputContent)
            mRecyclerView.postDelayed({ mRecyclerView.scrollToPosition(mArrayList.count() - 1) },250)
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        val itemDecoration = super.getItemDecoration() as DividerItemDecoration
        itemDecoration.setDrawable(ColorDrawable(Color.WHITE))
        return itemDecoration
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        val layoutManager = super.getLayoutManager() as LinearLayoutManager
        layoutManager.stackFromEnd = true
        return layoutManager
    }

    override fun onRefresh() {
        super.onRefresh()
        (0..10).forEach { it ->
            val msgItem = ChatMsgItem()
            msgItem.setType(if (it % 2 == 0) ChatMsgItem.FROM_MSG_FLAG else ChatMsgItem.SEND_MSG_FLAG)
            mArrayList.add(msgItem)
        }
        mBaseAdapter.notifyDataSetChanged()
    }
}