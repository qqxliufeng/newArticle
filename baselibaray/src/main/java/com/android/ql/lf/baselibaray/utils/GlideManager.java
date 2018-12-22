package com.android.ql.lf.baselibaray.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.ql.lf.baselibaray.R;
import com.android.ql.lf.baselibaray.data.ImageBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import com.bumptech.glide.signature.StringSignature;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import java.io.File;
import java.util.UUID;

/**
 * Created by lf on 2017/11/22 0022.
 *
 * @author lf on 2017/11/22 0022
 */

public class GlideManager {

    public static void loadImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(getImageUrl(path))
                .error(R.drawable.img_glide_load_default)
                .placeholder(R.drawable.img_glide_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadLocalImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path)
                .error(R.drawable.img_glide_load_default)
                .placeholder(R.drawable.img_glide_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadRoundImage(Context context, String path, ImageView imageView, int rounded) {
        Glide.with(context)
                .load(getImageUrl(path))
                .error(R.drawable.img_glide_load_default)
                .placeholder(R.drawable.img_glide_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, rounded, 0))
                .into(imageView);
    }


    public static void loadRoundImage(Context context, ImageBean imageBean, ImageView imageView, int rounded) {
        Glide.with(context)
                .load(getImageUrl(imageBean.getUriPath()))
                .error(R.drawable.img_glide_load_default)
                .placeholder(R.drawable.img_glide_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, rounded, 0))
                .into(imageView);
    }


    public static void loadFaceCircleImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(getImageUrl(path))
                .error(R.drawable.img_glide_circle_load_default)
                .placeholder(R.drawable.img_glide_circle_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(context), new CenterCrop(context))
                .into(imageView);
    }

    public static void loadLocalFaceCircleImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .error(R.drawable.img_glide_circle_load_default)
                .placeholder(R.drawable.img_glide_circle_load_default)
                .bitmapTransform(new CropCircleTransformation(context), new CenterCrop(context))
                .into(imageView);
    }

    public static void loadCircleImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(getImageUrl(path))
                .error(R.drawable.img_glide_circle_load_default)
                .placeholder(R.drawable.img_glide_circle_load_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(context), new CenterCrop(context))
                .into(imageView);
    }

    public static String getImageUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (TextUtils.isDigitsOnly(url)) {
            return BaseConfig.BASE_IP + "api/echopic?id=" + url;
        }
        if (url.startsWith("http://") || url.startsWith("http://")) {
            return url;
        } else {
            return BaseConfig.BASE_IP + url;
        }
    }


}
