package com.android.ql.lf.article.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.jsonToUserInfo
import com.android.ql.lf.article.data.postUserInfo
import com.android.ql.lf.article.ui.activity.MainActivity
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.fragment_login_first_step_layout.*
import kotlinx.android.synthetic.main.fragment_login_second_step_layout.*
import kotlinx.android.synthetic.main.layout_pre_step.*
import okhttp3.MultipartBody
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import top.zibin.luban.OnCompressListener
import java.io.File

open class LoginSecondStepFragment : BaseNetWorkingFragment() {

    private var facePath = ""

    protected val sex by lazy {
        PreferenceUtils.getPrefString(mContext, "sex", "")
    }

    protected val birthday by lazy {
        PreferenceUtils.getPrefString(mContext, "birthday", "")
    }

    protected val classify by lazy {
        PreferenceUtils.getPrefString(mContext, "my_classify", "")
    }

    private val phone by lazy {
        PreferenceUtils.getPrefString(mContext, "user_phone", "")
    }

    private val password by lazy {
        PreferenceUtils.getPrefString(mContext, "user_password", "")
    }

    private val code by lazy {
        PreferenceUtils.getPrefString(mContext, "user_code", "")
    }

    override fun getLayoutId() = R.layout.fragment_login_second_step_layout

    override fun initView(view: View?) {
        mTvPreFirstStep.setOnClickListener {
            (parentFragment as LoginFragment).positionFragment(0)
        }
        mIvLoginUserFace.setOnClickListener {
            openImageChoose(MimeType.ofImage(), 1)
        }
        mBtComplement.setOnClickListener {
            if (facePath == "") {
                toast("请选择头像")
                return@setOnClickListener
            }
            if (mEtLoginUserNickName.isEmpty()) {
                toast("请输入昵称")
                return@setOnClickListener
            }
            mPresent.getDataByPost(
                0x1, getBaseParamsWithModAndAct(LOGIN_MODULE, REGISTERDO_ACT)
                    .addParam("phone", phone)
                    .addParam("code", code)
                    .addParam("sex", sex)
                    .addParam("birthday", birthday)
                    .addParam("password", password)
                    .addParam("nickname", mEtLoginUserNickName.getTextString())
                    .addParam("address", "")
                    .addParam("classify", classify)
                    .addParam("pic", facePath)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Observable.just(Matisse.obtainPathResult(data)[0]).map {
                val outDir = File("${BaseConfig.IMAGE_PATH}face/")
                if (!outDir.exists()) {
                    outDir.mkdirs()
                }
                val out = File(outDir, "${System.currentTimeMillis()}.jpg")
                ImageFactory.compressAndGenImage(Matisse.obtainPathResult(data)[0], out.absolutePath, 200, false)
                out
            }.subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    getFastProgressDialog("正在上传头像……")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                        override fun onActionStart() {

                        }

                        override fun onActionEnd(builder: MultipartBody.Builder?) {
                            mPresent.uploadFile(0x0, T_MODULE, UPLOADING_PIC_ACT, builder?.build()?.parts())
                        }

                        override fun onActionFailed() {
                            toast("头像上传失败")
                        }

                    }).upload(arrayListOf(ImageBean(null, it.absolutePath ?: "")))
                }
        }
    }

    override fun onRequestStart(requestID: Int) {
        if (requestID == 0x1) {
            getFastProgressDialog("正在注册……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val json = JSONObject(result as String)
            val code = json.optInt("code")
            if (code == 200) {
                facePath = json.optString("result")
                GlideManager.loadFaceCircleImage(mContext, facePath, mIvLoginUserFace)
            } else {
                toast("头像上传失败")
            }
        } else if (requestID == 0x1) {
            val check = checkResultCode(result)
            if (check != null) {
                if (check.code == SUCCESS_CODE) {
                    val jsonObject = (check.obj as JSONObject).optJSONObject(RESULT_OBJECT)
                    if (UserInfo.jsonToUserInfo(jsonObject)) {
                        toast("注册成功")
                        UserInfo.postUserInfo()
                        startActivity(Intent(mContext, MainActivity::class.java))
                        finish()
                    } else {
                        toast("注册失败")
                    }
                } else {
                    toast((check.obj as JSONObject).optString("msg"))
                }
            }
        }
    }
}