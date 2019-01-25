package com.android.ql.lf.article.ui.fragments.community

import android.content.Context
import android.view.View
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.fragments.mine.PersonalIndexFragment
import com.android.ql.lf.article.utils.MESSAGE_MODULE
import com.android.ql.lf.article.utils.MYLEAVELIST_ACT
import com.android.ql.lf.article.utils.getBaseParamsWithPage
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyLeaveInfoFragment : BaseRecyclerViewFragment<MyLeaveInfoBean>() {

    companion object {
        fun startMyLeaveInfoFragment(context: Context){
            FragmentContainerActivity.from(context).setTitle("我的留言").setHiddenToolBar(false).setNeedNetWorking(true).setClazz(MyLeaveInfoFragment::class.java).start()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<MyLeaveInfoBean, BaseViewHolder> =
        object : BaseQuickAdapter<MyLeaveInfoBean, BaseViewHolder>(R.layout.adapter_my_leave_info_item_layout, mArrayList) {
            override fun convert(helper: BaseViewHolder?, item: MyLeaveInfoBean?) {
                GlideManager.loadFaceCircleImage(mContext,item?.leave_userData?.member_pic,helper?.getView(R.id.mIvLeaveMessageItemUserFace))
                val nickName = helper?.getView<TextView>(R.id.mTvLeaveMessageItemUserNickName)
                helper?.addOnClickListener(R.id.mTvLeaveMessageItemUserNickName)
                helper?.addOnClickListener(R.id.mIvLeaveMessageItemUserFace)
                nickName?.text = item?.leave_userData?.member_nickname
                helper?.setText(R.id.mCLLArticleReplyInfoItemReplyContainer,item?.leave_content)
                helper?.setText(R.id.mTvLeaveMessageItemTime,item?.leave_times)
            }
        }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE, MYLEAVELIST_ACT,currentPage))
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        processList(result as String,MyLeaveInfoBean::class.java)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MESSAGE_MODULE, MYLEAVELIST_ACT,currentPage))
    }

    override fun onMyItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemChildClick(adapter, view, position)
        if (view?.id == R.id.mTvLeaveMessageItemUserNickName || view?.id == R.id.mIvLeaveMessageItemUserFace){
            PersonalIndexFragment.startPersonalIndexFragment(mContext,mArrayList[position].leave_userData?.member_id ?: 0)
        }
    }
}

class MyLeaveInfoBean {
    var leave_cuid: Int = 0
    var leave_id: Int = 0
    var leave_uid: Int = 0
    var leave_times: String = ""
    var leave_content: String = ""
    var leave_type: Int = 0
    var member_id: Int = 0
    var leave_userData: MyLeaveInfoUserData? = null
}

class MyLeaveInfoUserData {
    var member_id: Int = 0
    var member_nickname: String = ""
    var member_pic: String = ""
}