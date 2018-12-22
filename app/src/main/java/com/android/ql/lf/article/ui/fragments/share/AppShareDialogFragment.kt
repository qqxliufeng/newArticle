package com.android.ql.lf.article.ui.fragments.share

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleShareItem
import com.android.ql.lf.article.utils.ThirdShareManager
import com.android.ql.lf.baselibaray.data.BaseShareItem
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import kotlinx.android.synthetic.main.dialog_app_share_layout.*
import org.jetbrains.anko.support.v4.toast

open class AppShareDialogFragment : BottomSheetDialogFragment() {

    protected var iWxApi : IWXAPI? = null

    protected var shareHandler:WbShareHandler? = null

    protected var baseShareItem : BaseShareItem? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_app_share_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvAppShareCancel?.setOnClickListener { dismiss() }
        mTvAppShareOpenWithBrowser?.setOnClickListener {
            openWithBrowser()
            dismiss()
        }
        mTvAppShareCopy?.setOnClickListener {
            copyBroad()
            dismiss()
        }
        mTvAppShareMore?.setOnClickListener {
            dismiss()
            shareMore()
        }
        mTvAppShareWeiBo?.setOnClickListener {
            dismiss()
            webiboShare()
        }
        mTvAppShareWXFriend?.setOnClickListener {
            dismiss()
            shareWX()
        }
        mTvAppShareWXCircle?.setOnClickListener {
            dismiss()
            shareCircle()
        }
    }

    private fun shareCircle() {
        if (iWxApi!=null && baseShareItem!=null) {
            ThirdShareManager.wxShareApp(iWxApi,BitmapFactory.decodeResource(resources,R.drawable.ic_launcher),SendMessageToWX.Req.WXSceneTimeline,baseShareItem)
        }
    }

    private fun shareWX() {
        if (iWxApi!=null && baseShareItem!=null) {
            ThirdShareManager.wxShareApp(iWxApi,BitmapFactory.decodeResource(resources,R.drawable.ic_launcher),SendMessageToWX.Req.WXSceneSession,baseShareItem)
        }
    }

    open fun setWeiBoShareHandler(shareHandler: WbShareHandler){
        this.shareHandler = shareHandler
    }

    fun setWxApi(iwxapi: IWXAPI){
        this.iWxApi = iwxapi
    }

    open fun setShareArticle(baseShareItem : BaseShareItem){
        this.baseShareItem = baseShareItem
    }

    open fun webiboShare(){
        shareHandler?.registerApp()
        shareHandler?.setProgressColor(ContextCompat.getColor(context!!,R.color.colorAccent))
        shareHandler?.shareMessage(ThirdShareManager.getWebpageObj(context!!,resources.getString(R.string.app_name),baseShareItem?.content ?: "",baseShareItem?.url ?: ""),false)
    }

    open fun shareMore() {
        if (baseShareItem!=null) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            sendIntent.putExtra(Intent.EXTRA_TEXT, baseShareItem?.content)
            startActivity(Intent.createChooser(sendIntent, "share"))
        }
    }

    open fun openWithBrowser() {
        if (baseShareItem!=null) {
            val browserIntent = Intent()
            browserIntent.data = Uri.parse(baseShareItem?.url)
            browserIntent.action = Intent.ACTION_VIEW
            startActivity(browserIntent)
        }
    }

    open fun copyBroad() {
        if (baseShareItem!=null) {
            val copyBroadManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            copyBroadManager.text = baseShareItem?.url ?: ""
            toast("复制成功")
        }
    }
}