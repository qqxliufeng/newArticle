package com.android.ql.lf.article.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.android.ql.lf.article.R;
import com.android.ql.lf.article.data.ArticleShareItem;
import com.android.ql.lf.article.data.PersonalShareItem;
import com.android.ql.lf.baselibaray.data.BaseShareItem;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;


public class ThirdShareManager {

//    public static void qqShare(Activity context, Tencent mTencent, IUiListener uiListener, ArticleShareItem articleShareItem) {
//        final Bundle params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, getShareTitle(context));
//
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getShareContent(context));
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, articleShareItem.getUrl());
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, UserInfo.getInstance().getSharePic());
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getPackageName());
//        mTencent.shareToQQ(context, params, uiListener);
//    }

//    public static void zoneShare(Activity context, Tencent mTencent, IUiListener uiListener) {
//        final Bundle params = new Bundle();
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, getShareTitle(context));//必填
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, getShareContent(context));//选填
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UserInfo.getInstance().getShareUrl());//必填
//        ArrayList<String> images = new ArrayList<String>();
//        images.add(UserInfo.getInstance().getSharePic());
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);
//        mTencent.shareToQzone(context, params, uiListener);
//    }


    public static void wxShare(IWXAPI api, Bitmap bitmap, int type, ArticleShareItem articleShareItem) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = articleShareItem.getUrl();
        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.description = articleShareItem.getContent();
        wxMediaMessage.title = articleShareItem.getTitle();
//        wxMediaMessage.thumbData = bmpToByteArray(Bitmap.createScaledBitmap(bitmap, 150, 150, true), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = wxMediaMessage;
        req.scene = type;
        api.sendReq(req);
    }

    public static void wxShareApp(IWXAPI api, Bitmap bitmap, int type, BaseShareItem articleShareItem) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = articleShareItem.getUrl();
        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.description = articleShareItem.getContent();
        wxMediaMessage.title = articleShareItem.getTitle();
        wxMediaMessage.thumbData = bmpToByteArray(Bitmap.createScaledBitmap(bitmap, 150, 150, true), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = wxMediaMessage;
        req.scene = type;
        api.sendReq(req);
    }

    public static void wxSharePersonalIndex(IWXAPI api, Bitmap bitmap, int type, PersonalShareItem personalShareItem) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = personalShareItem.getUrl();
        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.description = personalShareItem.getContent();
        wxMediaMessage.title = personalShareItem.getTitle();
        wxMediaMessage.thumbData = bmpToByteArray(Bitmap.createScaledBitmap(bitmap, 150, 150, true), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = wxMediaMessage;
        req.scene = type;
        api.sendReq(req);
    }

    public static void wxShareImage(IWXAPI api, String path,int scene){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        WXImageObject imageObject = new WXImageObject();
        imageObject.setImagePath(path);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = imageObject;
        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
        bitmap.recycle();
        wxMediaMessage.thumbData = bmpToByteArray(thumbBitmap,true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = wxMediaMessage;
        req.scene = scene;
        api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


//    public static String getShareTitle(Context context){
//        String shareTitle = context.getResources().getString(R.string.app_name);
//        if (!TextUtils.isEmpty(UserInfo.getInstance().getShareTitle())){
//            shareTitle = UserInfo.getInstance().getShareTitle();
//        }
//        return shareTitle;
//    }
//
//    public static String getShareContent(Context context){
//        String shareContent = context.getResources().getString(R.string.app_name);
//        if (!TextUtils.isEmpty(UserInfo.getInstance().getShareIntro())){
//            shareContent = UserInfo.getInstance().getShareIntro();
//        }
//        return shareContent;
//    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    public static WeiboMultiMessage getWebpageObj(Context context, String title, String description, String actionUrl) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = description;
        weiboMultiMessage.mediaObject = mediaObject;
        return weiboMultiMessage;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    public static WeiboMultiMessage getWebpageObjWithBitmap(String title, String description, String actionUrl,Bitmap bitmap) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = description;
        weiboMultiMessage.mediaObject = mediaObject;
        return weiboMultiMessage;
    }

    /**
     * 创建图片消息对象。
     * @return 图片消息对象。
     */
    public static WeiboMultiMessage getImageObj(String imagePath) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
//        Bitmap  bitmap = BitmapFactory.decodeFile(imagePath);
        imageObject.imagePath = imagePath;
        weiboMessage.imageObject = imageObject;
        return weiboMessage;
    }
}
