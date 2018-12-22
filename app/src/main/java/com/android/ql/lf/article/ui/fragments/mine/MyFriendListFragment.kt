package com.android.ql.lf.article.ui.fragments.mine

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast

/**
 * Created by lf on 18.11.24.
 * @author lf on 18.11.24
 */
class MyFriendListFragment : BaseRecyclerViewFragment<FriendItemBean>() {

    companion object {
        fun startFriendListFragment(context: Context,title:String = "",content:String = "",aid:Int = 0){
            FragmentContainerActivity.from(context).setTitle("分享好友").setExtraBundle(bundleOf(Pair("title",title),
                Pair("content",content),
                Pair("aid",aid)
            )).setClazz(MyFriendListFragment::class.java).setNeedNetWorking(true).start()
        }
    }

    private val title by lazy {
        arguments?.getString("title") ?: ""
    }

    private val content by lazy {
        arguments?.getString("content") ?: ""
    }

    private val aid by lazy {
        arguments?.getInt("aid") ?: 0
    }

    override fun createAdapter() = object : BaseQuickAdapter<FriendItemBean,BaseViewHolder>(R.layout.adapter_my_friend_list_item_layout,mArrayList) {
        override fun convert(helper: BaseViewHolder?, item: FriendItemBean?) {
            GlideManager.loadFaceCircleImage(mContext,item?.like_userData?.member_pic,helper?.getView(R.id.mIvMyFriendListItemFace))
            helper?.setText(R.id.mTvMyFriendListItemNickName,item?.like_userData?.member_nickname)
            helper?.setText(R.id.mTvMyFriendListItemDes,"${item?.like_articleCount}篇笔记，${item?.like_fanCount}粉丝")
        }
    }

    override fun initView(view: View?) {
        super.initView(view)
        setRefreshEnable(false)
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MEMBER_MODULE, MY_LIKE_FRIEND_ACT,currentPage))
    }

    override fun onLoadMore() {
        super.onLoadMore()
        mPresent.getDataByPost(0x0, getBaseParamsWithPage(MEMBER_MODULE, MY_LIKE_FRIEND_ACT,currentPage))
    }

    override fun getEmptyMessage(): String {
        return "暂无好友"
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x1) getFastProgressDialog("正在分享给好友……")
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            processList(result as String,FriendItemBean::class.java)
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check!=null){
                if (check.code == SUCCESS_CODE){
                    toast("分享好友成功")
                    finish()
                }else{
                    toast("分享好友失败")
                }
            }else{
                toast("分享好友失败")
            }
        }
    }


    override fun onMyItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemClick(adapter, view, position)
        val shareDialog = Dialog(mContext)
        val shareContentView = View.inflate(mContext,R.layout.dialog_share_content_layout,null)
        shareDialog.setContentView(shareContentView)
        shareContentView.findViewById<TextView>(R.id.mTvShareTitle).text = title
        shareContentView.findViewById<TextView>(R.id.mTvShareContent).text = content
        shareContentView.findViewById<Button>(R.id.mBtShareCancel).setOnClickListener {
            shareDialog.dismiss()
        }
        shareContentView.findViewById<Button>(R.id.mBtShareSubmit).setOnClickListener {
            shareDialog.dismiss()
            val item = mArrayList[position]
            mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MESSAGE_MODULE,LEAVE_SHARE_DO_ACT)
                .addParam("cuid",item.like_userData?.member_id)
                .addParam("theme",aid))
        }
        shareDialog.show()
    }
}

class FriendItemBean {
    var like_id: Int = 0
    var like_theme: Int = 0
    var like_uid: Int = 0
    var like_reuid: Int = 0
    var like_status: Int = 0
    var like_articleCount: Int = 0
    var like_fanCount: Int = 0
    var like_likeStatus: Int = 0
    var like_userData: FriendUserDataInfoBean? = null
}

class FriendUserDataInfoBean {
    var member_id: Int = 0
    var member_phone: String? = null
    var member_pass: String? = null
    var member_nickname: String? = null
    var member_pic: String? = null
    var member_balance: Int = 0
    var member_fans: Int = 0
    var member_tags: String? = null
    var member_status: Int = 0
    var member_address: Int = 0
    var member_classify: String? = null
    var member_age: Int = 0
    var member_birthday: String? = null
    var member_sex: Int = 0
    var member_push: Int = 0
    var member_signature: String? = null
    var member_front: String? = null
    var member_reverse: String? = null
    var member_leave: String? = null
}
