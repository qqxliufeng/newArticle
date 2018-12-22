package com.android.ql.lf.article.ui.fragments.share

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.ThirdShareManager
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.dialog_image_poster_share_layout.*
import kotlinx.android.synthetic.main.fragment_image_poster_share_layout.*
import org.jetbrains.anko.bundleOf
import java.io.File

/**
 * Created by lf on 18.11.24.
 * @author lf on 18.11.24
 */
class ImagePosterShareFragment : BaseNetWorkingFragment() {

    companion object {
        fun startImagePosterShareFragment(context: Context, imagePath: String) {
            FragmentContainerActivity.from(context).setTitle("生成海报")
                .setExtraBundle(bundleOf(Pair("imagePath", imagePath))).setNeedNetWorking(true)
                .setClazz(ImagePosterShareFragment::class.java).start()
        }
    }

    private val imagePosterShareFragment by lazy {
        ImagePosterShareDialogFragment()
    }


    private val imagePath by lazy {
        arguments?.getString("imagePath") ?: ""
    }

    private val iwxapi by lazy {
        WXAPIFactory.createWXAPI(mContext,BaseConfig.WX_APP_ID,true)
    }

    private val weiboShareHandler by lazy {
        WbShareHandler(mContext as Activity)
    }

    private var desFile: File? = null

    override fun getLayoutId() = R.layout.fragment_image_poster_share_layout

    override fun initView(view: View?) {
        mWvImagePostShare.settings.allowFileAccess = true
        mWvImagePostShare.settings.allowFileAccessFromFileURLs = true
        mWvImagePostShare.settings.allowUniversalAccessFromFileURLs = true
        mWvImagePostShare.loadDataWithBaseURL(null,"""<img src="file://$imagePath" style="width:100%"/>""","text/html","UTF-8",null)
        mBtImagePosterSave.setOnClickListener {
            if (desFile == null) {
                val dirFile = File("${BaseConfig.IMAGE_PATH}savepost/")
                if (!dirFile.exists()){
                    dirFile.mkdirs()
                }
                desFile = File(dirFile,"${System.currentTimeMillis()}.jpg")
                if (!desFile!!.exists()){
                    desFile!!.createNewFile()
                }
                val srcFile = File(imagePath)
                srcFile.copyTo(desFile!!,true)
                Snackbar.make(mBtImagePosterSave.parent.parent.parent as RelativeLayout,"图片保存在${BaseConfig.IMAGE_PATH}savepost/目录下",Snackbar.LENGTH_LONG)
                    .setAction("确定",null).show()
            }else{
                Snackbar.make(mBtImagePosterSave.parent.parent.parent as RelativeLayout,"图片保存在${BaseConfig.IMAGE_PATH}savepost/目录下",Snackbar.LENGTH_LONG)
                    .setAction("确定",null).show()
            }
        }
        mBtImagePosterShare.setOnClickListener {
            imagePosterShareFragment.setWxApi(iwxapi)
            imagePosterShareFragment.setShareImagePath(imagePath)
            imagePosterShareFragment.setWeiBoShareHandler(weiboShareHandler)
            imagePosterShareFragment.show(childFragmentManager,"share_image_post_dialog")
        }
    }
}

class ImagePosterShareDialogFragment : AppShareDialogFragment(){

    private var shareImagePath:String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_image_poster_share_layout, container, false)
    }

    fun setShareImagePath(imagePath:String){
        this.shareImagePath = imagePath
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTvAppShareCancel.setOnClickListener {
            dismiss()
        }
        mTvImagePosterWX.setOnClickListener {
            dismiss()
            if (shareImagePath!=null) {
                ThirdShareManager.wxShareImage(iWxApi, shareImagePath, SendMessageToWX.Req.WXSceneSession)
            }
        }
        mTvImagePosterCircle.setOnClickListener {
            if (shareImagePath!=null) {
                ThirdShareManager.wxShareImage(iWxApi, shareImagePath, SendMessageToWX.Req.WXSceneTimeline)
            }
            dismiss()
        }
        mTvImagePosterWB.setOnClickListener {
            if (shareImagePath!=null) {
                shareHandler?.registerApp()
                shareHandler?.setProgressColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                shareHandler?.shareMessage(ThirdShareManager.getImageObj(shareImagePath), false)
            }
            dismiss()
        }
    }
}