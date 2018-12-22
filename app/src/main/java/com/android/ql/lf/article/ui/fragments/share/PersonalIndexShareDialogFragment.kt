package com.android.ql.lf.article.ui.fragments.share

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleShareItem
import com.android.ql.lf.article.data.PersonalShareItem
import com.android.ql.lf.article.utils.ThirdShareManager
import com.android.ql.lf.baselibaray.utils.BaseConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import kotlinx.android.synthetic.main.dialog_personal_share_layout.*
import java.lang.Exception

/**
 * Created by lf on 18.11.26.
 * @author lf on 18.11.26
 */
class PersonalIndexShareDialogFragment : AppShareDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_personal_share_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvAppShareWXFriend.setOnClickListener {
            shareArticleToWXFriend()
        }
        mTvAppShareWXCircle.setOnClickListener {
            shareArticleToWXCircle()
        }
        mTvArticleShareWeibo.setOnClickListener {
            webiboShare()
        }
    }


    private fun shareArticleToWXFriend() {
        if (baseShareItem != null) {
            val personalShareItem = baseShareItem as PersonalShareItem
            Glide.with(this).load("${BaseConfig.BASE_IP}${personalShareItem.shareImagePath}").asBitmap()
                .into(object : SimpleTarget<Bitmap>(150, 150) {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        if (resource != null) {
                            ThirdShareManager.wxSharePersonalIndex(
                                iWxApi,
                                resource,
                                SendMessageToWX.Req.WXSceneSession,
                                baseShareItem as PersonalShareItem
                            )
                        }
                        dismiss()
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        dismiss()
                        super.onLoadFailed(e, errorDrawable)
                    }
                })
        }
    }

    private fun shareArticleToWXCircle() {
        if (baseShareItem != null) {
            val personalShareItem = baseShareItem as PersonalShareItem
            Glide.with(this).load("${BaseConfig.BASE_IP}${personalShareItem.shareImagePath}").asBitmap()
                .into(object : SimpleTarget<Bitmap>(150, 150) {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        if (resource != null) {
                            ThirdShareManager.wxSharePersonalIndex(
                                iWxApi,
                                resource,
                                SendMessageToWX.Req.WXSceneTimeline,
                                baseShareItem as PersonalShareItem
                            )
                        }
                        dismiss()
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        dismiss()
                    }
                })
        }
        dismiss()
    }

    override fun webiboShare() {
        if (baseShareItem != null) {
            val personalShareItem = baseShareItem as PersonalShareItem
            Glide.with(this).load("${BaseConfig.BASE_IP}${personalShareItem.shareImagePath}").asBitmap().into(object :
                SimpleTarget<Bitmap>(100,100) {
                override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                    if (resource!=null) {
                        shareHandler?.registerApp()
                        shareHandler?.setProgressColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                        shareHandler?.shareMessage(
                            ThirdShareManager.getWebpageObjWithBitmap(
                                baseShareItem?.title ?: "",
                                baseShareItem?.content ?: "",
                                baseShareItem?.url ?: "",
                                resource
                            ), false
                        )
                    }
                    dismiss()
                }

                override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    super.onLoadFailed(e, errorDrawable)
                    dismiss()
                }
            })
        }
    }
}