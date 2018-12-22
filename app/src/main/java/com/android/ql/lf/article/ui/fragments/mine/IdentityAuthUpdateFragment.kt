package com.android.ql.lf.article.ui.fragments.mine

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.view.Menu
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.AuthStatus
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.ui.widgets.SingleTextViewActionProvide
import com.android.ql.lf.article.utils.MEMBER_MODULE
import com.android.ql.lf.article.utils.PERSONAL_CERT_ACT
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.ImageUploadHelper
import kotlinx.android.synthetic.main.fragment_identity_auth_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.support.v4.toast

/**
 * Created by lf on 18.11.21.
 * @author lf on 18.11.21
 */
class IdentityAuthUpdateFragment : IdentityAuthFragment() {

    companion object {
        fun startIdentityAuthUpdateFragment(context: Context){
            FragmentContainerActivity
                .from(context)
                .setNeedNetWorking(true)
                .setClazz(IdentityAuthUpdateFragment::class.java)
                .setTitle("身份认证")
                .start()
        }
    }


    override fun initView(view: View?) {
        super.initView(view)
        mLlIdentityAuthBottomContainer.visibility = View.GONE
        GlideManager.loadImage(mContext,UserInfo.user_front,mIvIdentityAuthZM)
        GlideManager.loadImage(mContext,UserInfo.user_reverse,mIvIdentityAuthFM)
        when(AuthStatus.getNameByFlag(UserInfo.user_status)){
            AuthStatus.WAIT_AUTH->{
                mTvIdentityAuthZM.text = "审核中，请耐心等待……"
                mTvIdentityAuthFM.text = "审核中，请耐心等待……"
                mRlIdentityAuthZM.isEnabled = false
                mRlIdentityAuthFM.isEnabled = false
            }
            AuthStatus.COMPLEMENT_AUTH->{
                mTvIdentityAuthZM.text = "审核成功"
                mTvIdentityAuthFM.text = "审核成功"
                mRlIdentityAuthZM.isEnabled = false
                mRlIdentityAuthFM.isEnabled = false
            }
            AuthStatus.FAIL_AUTH->{
                mTvIdentityAuthZM.text = "审核失败，请修改"
                mTvIdentityAuthFM.text = "审核失败，请修改"
                mRlIdentityAuthZM.isEnabled = true
                mRlIdentityAuthFM.isEnabled = true
            }
            else -> {
                mTvIdentityAuthZM.text = "审核中，请耐心等待……"
                mTvIdentityAuthFM.text = "审核中，请耐心等待……"
                mRlIdentityAuthZM.isEnabled = false
                mRlIdentityAuthFM.isEnabled = false
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val submitMenu = menu?.findItem(R.id.mMenuSubmit)
        val provider = MenuItemCompat.getActionProvider(submitMenu) as SingleTextViewActionProvide
        provider.setOnActionClick {
            if (zmImageBean == null && fmImageBean == null){
                toast("请选择要修改的照片")
                return@setOnActionClick
            }
            imageList.clear()
            keyList.clear()
            if (zmImageBean != null && fmImageBean != null) {
                imageList.add(0, zmImageBean!!)
                keyList.add(0, "front")
                imageList.add(1,fmImageBean!!)
                keyList.add(1,"reverse")
            }else if (zmImageBean != null && fmImageBean == null){
                imageList.add(0,zmImageBean!!)
                keyList.add(0,"front")
            }else if (zmImageBean == null && fmImageBean != null){
                imageList.add(0,fmImageBean!!)
                keyList.add(0,"reverse")
            }
            ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                override fun onActionStart() {
                }

                override fun onActionEnd(builder: MultipartBody.Builder?) {
                    builder?.addFormDataPart("uid", UserInfo.user_id.toString())
                    mPresent.uploadFile(0x0, MEMBER_MODULE, PERSONAL_CERT_ACT,builder?.build()?.parts())
                }

                override fun onActionFailed() {
                }

            }).upload(imageList, keyList)
        }
        when(AuthStatus.getNameByFlag(UserInfo.user_status)){
            AuthStatus.WAIT_AUTH->{
                provider.setTitle("已提交")
                provider.setTextColor(ContextCompat.getColor(mContext,R.color.grayTextColor))
                provider.isEnable(false)
            }
            AuthStatus.COMPLEMENT_AUTH->{
                provider.setTitle("已提交")
                provider.setTextColor(ContextCompat.getColor(mContext,R.color.grayTextColor))
                provider.isEnable(false)
            }
            AuthStatus.FAIL_AUTH->{
                provider.setTitle("提交")
                provider.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent))
                provider.isEnable(true)
            }
            else -> {
                provider.setTitle("已提交")
                provider.setTextColor(ContextCompat.getColor(mContext,R.color.grayTextColor))
                provider.isEnable(false)
            }
        }
    }

}