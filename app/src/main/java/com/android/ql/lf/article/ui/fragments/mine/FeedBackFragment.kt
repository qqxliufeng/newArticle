package com.android.ql.lf.article.ui.fragments.mine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.ImageUploadHelper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_feed_back_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast
import java.util.ArrayList

/**
 * Created by lf on 18.11.20.
 * @author lf on 18.11.20
 */
class FeedBackFragment : BaseNetWorkingFragment() {

    companion object {
        fun startFeedBackFragment(context: Context,type:Int) {
            FragmentContainerActivity
                .from(context)
                .setClazz(FeedBackFragment::class.java)
                .setTitle("意见反馈")
                .setNeedNetWorking(true)
                .setExtraBundle(bundleOf(Pair("type",type)))
                .start()
        }
    }

    val MAX_IMAGE_SIZE = 4

    private val type by lazy {
        arguments?.getInt("type") ?: 0
    }


    private val imageList = arrayListOf<ImageBean>()

    override fun getLayoutId() = R.layout.fragment_feed_back_layout

    override fun initView(view: View?) {
        if(type == 2){
            mTvFeedBackImageDes.text = "若反馈充值未到账，请上传充值结果截图（如成功扣费的短信，苹果的收据邮件截图等）"
        }
        imageList.add(ImageBean(R.drawable.img_select_img_icon,null))
        mRvFeedBackImageContent.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        mRvFeedBackImageContent.adapter = object :
            BaseQuickAdapter<ImageBean, BaseViewHolder>(R.layout.adapter_select_image_item_layout, imageList) {
            override fun convert(helper: BaseViewHolder?, item: ImageBean?) {
                if (item!!.uriPath == null) {
                    helper?.setImageResource(R.id.mIvSelectImageItem, R.drawable.img_select_img_icon)
                } else {
                    GlideManager.loadLocalImage(mContext, item.uriPath, helper?.getView<ImageView>(R.id.mIvSelectImageItem))
                }
            }
        }
        mRvFeedBackImageContent.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                if (imageList[position].resId != null) {
                    if (imageList.size == MAX_IMAGE_SIZE + 1){
                        return
                    }
                    openImageChoose(MimeType.ofImage(),MAX_IMAGE_SIZE - (imageList.size - 1))
                }
            }
        })
        mBtFeedBackSubmit.setOnClickListener {
            if (mEtFeedBackContent.isEmpty()) {
                toast("请输入意见")
                return@setOnClickListener
            }
            val concat = if(TextUtils.isEmpty(mEtFeedBackPhone.getTextString())){""}else{mEtFeedBackPhone.getTextString()}
            if (imageList.size == 1){
                mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE, IDEA_DO_ACT)
                    .addParam("concat", concat)
                    .addParam("type",type)
                    .addParam("content", mEtFeedBackContent.getTextString()))
            }else {
                ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                    override fun onActionStart() {
                        getFastProgressDialog("正在上传问题~")
                    }

                    override fun onActionEnd(builder: MultipartBody.Builder?) {
                        builder?.addFormDataPart("concat", concat)
                        builder?.addFormDataPart("uid",UserInfo.user_id.toString())
                        builder?.addFormDataPart("type",type.toString())
                        builder?.addFormDataPart("content", mEtFeedBackContent.getTextString())
                        mPresent.uploadFile(0x0, MEMBER_MODULE, IDEA_DO_ACT, builder?.build()?.parts())
                    }

                    override fun onActionFailed() {
                    }
                }).uploadMultiImage(imageList.filter { it.uriPath != null } as ArrayList<ImageBean>)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
            Matisse.obtainPathResult(data).forEach {
                imageList.add(0, ImageBean(null,it))
            }
            mTvFeedBackImageCount.text = "${imageList.size - 1}/4"
            mRvFeedBackImageContent.adapter.notifyDataSetChanged()
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            val check = checkResultCode(result)
            if (check!=null){
                if (check.code == SUCCESS_CODE){
                    toast("提交成功，感谢您的宝贵意见~~")
                    finish()
                }else{
                    toast("提交失败，请重试……")
                }
            }else{
                toast("提交失败，请重试……")
            }
        }
    }
}