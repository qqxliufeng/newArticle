package com.android.ql.lf.article.ui.fragments.mine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.ui.widgets.SingleTextViewActionProvide
import com.android.ql.lf.article.utils.MEMBER_MODULE
import com.android.ql.lf.article.utils.PERSONAL_CERT_ACT
import com.android.ql.lf.article.utils.getBaseParams
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_identity_auth_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * Created by lf on 18.11.21.
 * @author lf on 18.11.21
 */
open class IdentityAuthFragment :BaseNetWorkingFragment(){

    companion object {
        fun startIdentityAuthFragment(context: Context){
            FragmentContainerActivity.from(context).setNeedNetWorking(true).setClazz(IdentityAuthFragment::class.java).setTitle("身份认证").start()
        }
    }

    protected var zmImageBean:ImageBean? = null
    protected var fmImageBean:ImageBean? = null

    protected val imageList = arrayListOf<ImageBean>()
    protected val keyList = arrayListOf<String>()

    protected var currentSelectFlag = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_identity_auth_layout

    override fun initView(view: View?) {
        mRlIdentityAuthZM.setOnClickListener {
            currentSelectFlag = 0
            openImageChoose(MimeType.ofImage(),1)
        }
        mRlIdentityAuthFM.setOnClickListener {
            currentSelectFlag = 1
            openImageChoose(MimeType.ofImage(),1)
        }
        val param = (mIvIdentityAuthPic.layoutParams as LinearLayout.LayoutParams)
        param.height = mContext.getScreen().first
        mIvIdentityAuthPic.layoutParams = param
        mIvIdentityAuthPic.setImageResource(R.drawable.img_glide_load_default)
        mPresent.getDataByGet(0x1, MEMBER_MODULE, PERSONAL_CERT_ACT, getBaseParams().addParam("token",BaseConfig.md5Token))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
                compressAndSaveCacheFace(Matisse.obtainPathResult(data)[0], object : OnCompressListener {
                    override fun onSuccess(file: File?) {
                        if (currentSelectFlag == 0){
                            zmImageBean = ImageBean(null,file?.absolutePath)
                            GlideManager.loadLocalImage(mContext,file?.absolutePath,mIvIdentityAuthZM)
                        }else{
                            fmImageBean = ImageBean(null,file?.absolutePath)
                            GlideManager.loadLocalImage(mContext,file?.absolutePath,mIvIdentityAuthFM)
                        }
                    }
                    override fun onError(e: Throwable?) {
                    }
                    override fun onStart() {
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.submit_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val submitMenu = menu?.findItem(R.id.mMenuSubmit)
        val provider = MenuItemCompat.getActionProvider(submitMenu) as SingleTextViewActionProvide
        provider.setOnActionClick {
            if (zmImageBean == null){
                toast("请选择正面照片")
                return@setOnActionClick
            }
            if (fmImageBean == null){
                toast("请选择反面照片")
                return@setOnActionClick
            }
            imageList.clear()
            keyList.clear()
            imageList.add(0,zmImageBean!!)
            keyList.add(0,"front")
            imageList.add(1,fmImageBean!!)
            keyList.add(1,"reverse")
            ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                override fun onActionStart() {
                }

                override fun onActionEnd(builder: MultipartBody.Builder?) {
                    builder?.addFormDataPart("uid",UserInfo.user_id.toString())
                    mPresent.uploadFile(0x0, MEMBER_MODULE, PERSONAL_CERT_ACT,builder?.build()?.parts())
                }

                override fun onActionFailed() {
                }

            }).upload(imageList, keyList)

        }
        provider.setTitle("提交")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x1){
            getFastProgressDialog("加载中……")
        }else {
            getFastProgressDialog("正在提交……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            val check = checkResultCode(result)
            if (check!=null){
                if (check.code == SUCCESS_CODE){
                    val json = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                    UserInfo.user_status = json.optInt("member_status")
                    UserInfo.user_front = json.optString("member_front")
                    UserInfo.user_reverse = json.optString("member_reverse")
                    UserInfo.user_leave = json.optString("member_leave")
                    toast("提交成功，等待审核")
                    finish()
                }else{
                    toast("提交失败，请重试……")
                }
            }else{
                toast("提交失败，请重试……")
            }
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check!=null) {
                if (check.code == SUCCESS_CODE) {
                    val json = (check.obj as JSONObject).optJSONObject("arr")
                    mTvIdentityAuthZMName.text = json.optString("identone")
                    mTvIdentityAuthFMName.text = json.optString("identtwo")
                    UserInfo.user_leave = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT).optString("member_leave")
                    if (UserInfo.user_leave!=null && UserInfo.user_leave!!.isNotEmpty()){
                        mTvIdentityAuthFailReason.text = "审核失败：${UserInfo.user_leave}"
                    }
                    val path = "${BaseConfig.BASE_IP}${json.optString("cert")}"
                    Glide.with(this).load(path).asBitmap().into(object :SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            val imageWidth = resource?.width ?: 1
                            val imageHeight = resource?.height ?: 1
                            val param = (mIvIdentityAuthPic.layoutParams as LinearLayout.LayoutParams)
                            param.height = (mIvIdentityAuthPic.measuredWidth * imageHeight) / imageWidth
                            mIvIdentityAuthPic.layoutParams = param
                            mIvIdentityAuthPic.setImageBitmap(resource)
                        }
                    })
                }
            }
        }
    }
}