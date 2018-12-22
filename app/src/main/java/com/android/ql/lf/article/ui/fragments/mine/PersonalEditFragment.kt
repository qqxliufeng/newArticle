package com.android.ql.lf.article.ui.fragments.mine

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.postUserInfo
import com.android.ql.lf.article.data.updateUserInfo
import com.android.ql.lf.article.utils.EDIT_USER_PIC_ACT
import com.android.ql.lf.article.utils.MEMBER_MODULE
import com.android.ql.lf.article.utils.PERSONAL_EDIT_ACT
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.ImageUploadHelper
import com.android.ql.lf.baselibaray.utils.compressAndSaveCacheFace
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_personal_edit_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class PersonalEditFragment : BaseNetWorkingFragment() {

    companion object {
        fun startPersonalEditFragment(context: Context) {
            FragmentContainerActivity.from(context).setNeedNetWorking(true).setTitle("编辑个人资料").setClazz(PersonalEditFragment::class.java).start()
        }
    }

    private var facePath = ""

    private var tempBirthday = UserInfo.user_birthday
    private var tempNickName = UserInfo.user_nickname?:""

    private val sexs = arrayOf("男", "女")

    override fun getLayoutId() = R.layout.fragment_personal_edit_layout

    override fun initView(view: View?) {
        GlideManager.loadFaceCircleImage(mContext,UserInfo.user_pic,mIvPersonalEditFace)
        mTvPersonalEditNickName.text = UserInfo.user_nickname
        mTvPersonalEditSex.text = when {
            UserInfo.user_sex == 1 -> "男"
            UserInfo.user_sex == 2 -> "女"
            else -> "请选择"
        }
        mRlPersonalEditFaceContainer.setOnClickListener {
            openImageChoose(MimeType.ofImage(), 1)
        }
        mRlPersonalEditNickNameContainer.setOnClickListener {
            val nickNameDialogFragment = PersonalEditNickNameDialogFragment()
            nickNameDialogFragment.myShow(childFragmentManager, "nick_name_dialog") { nickName ->
                tempNickName = nickName
                mPresent.getDataByPost(0x3, getBaseParamsWithModAndAct(MEMBER_MODULE,PERSONAL_EDIT_ACT)
                    .addParam("nickname",nickName))
            }
        }
        mRlPersonalEditSexContainer.setOnClickListener {
            var tempWhich = 0
            val sexDialog = AlertDialog.Builder(mContext)
            sexDialog.setTitle("请选择性别")
            sexDialog.setSingleChoiceItems(sexs, if(UserInfo.user_sex != 0) {UserInfo.user_sex - 1} else 0 ) { _, which ->
                tempWhich = which
            }
            sexDialog.setNegativeButton("取消",null)
            sexDialog.setPositiveButton("确定"){_,_->
                val sex = when {
                    UserInfo.user_sex == 1 -> "男"
                    UserInfo.user_sex == 2 -> "女"
                    else -> {
                        "请选择"
                    }
                }
                if (sexs[tempWhich] == sex){
                    return@setPositiveButton
                }
                mPresent.getDataByPost(0x1, getBaseParamsWithModAndAct(MEMBER_MODULE,PERSONAL_EDIT_ACT)
                    .addParam("sex",if (sexs[tempWhich] == "男") "1" else "2"))
            }
            sexDialog.create().show()
        }
        if (UserInfo.user_birthday == null || UserInfo.user_birthday == "") {
            mTvPersonalEditBirthday.text = "请选择"
        }else{
            mTvPersonalEditBirthday.text = UserInfo.user_birthday
        }
        mRlPersonalEditBirthdayContainer.setOnClickListener {
            val dataDialog = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                tempBirthday = "$year-$month-$dayOfMonth"
                mPresent.getDataByPost(0x2, getBaseParamsWithModAndAct(MEMBER_MODULE,PERSONAL_EDIT_ACT)
                    .addParam("birthday","$year.$month.$dayOfMonth"))
            }, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            dataDialog.show()
        }
        mTvPersonalEditDes.setOnClickListener {
            FragmentContainerActivity.from(mContext).setTitle("编辑个性签名").setNeedNetWorking(true).setClazz(PersonalDescriptionFragment::class.java).start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            compressAndSaveCacheFace(Matisse.obtainPathResult(data)[0], object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                        override fun onActionStart() {
                            getFastProgressDialog("正在上传头像……")
                        }

                        override fun onActionEnd(builder: MultipartBody.Builder?) {
                            builder?.addFormDataPart("uid","${UserInfo.user_id}")
                            mPresent.uploadFile(0x0, MEMBER_MODULE, EDIT_USER_PIC_ACT, builder?.build()?.parts())
                        }

                        override fun onActionFailed() {
                        }

                    }).upload(arrayListOf(ImageBean(null, file?.absolutePath ?: "")))
                }

                override fun onError(e: Throwable?) {
                }

                override fun onStart() {
                }
            })
        }
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x1){
            getFastProgressDialog("正在修改性别……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            if (requestID == 0x0) {
                val json = JSONObject(result as String)
                val code = json.optInt("code")
                if (code == 200) {
                    facePath = json.optString("result")
                    GlideManager.loadFaceCircleImage(mContext, facePath, mIvPersonalEditFace)
                    UserInfo.updateUserInfo(UserInfo.user_nickname?:"",facePath)
                } else {
                    toast("头像上传失败")
                }
            }
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check!=null && check.code == SUCCESS_CODE){
                toast("性别修改成功")
                if (UserInfo.user_sex == 1) { UserInfo.user_sex = 2 } else { UserInfo.user_sex = 1}
                mTvPersonalEditSex.text = if(UserInfo.user_sex == 1) "男"  else "女"
            }
        }else if (requestID == 0x2){
            val check = checkResultCode(result)
            if (check!=null && check.code == SUCCESS_CODE){
                toast("生日修改成功")
                UserInfo.user_birthday = tempBirthday
                mTvPersonalEditBirthday.text = tempBirthday
            }
        }else if (requestID == 0x3){
            val check = checkResultCode(result)
            if (check!=null && check.code == SUCCESS_CODE){
                toast("昵称修改成功")
                UserInfo.user_nickname = tempNickName
                mTvPersonalEditNickName.text = tempNickName
                UserInfo.updateUserInfo(tempNickName,UserInfo.user_pic?:"")
            }
        }
    }
}