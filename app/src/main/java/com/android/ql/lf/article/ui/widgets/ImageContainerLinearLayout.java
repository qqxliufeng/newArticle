package com.android.ql.lf.article.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.ql.lf.baselibaray.utils.GlideManager;

import java.util.ArrayList;

/**
 * Created by lf on 2017/12/13 0013.
 *
 * @author lf on 2017/12/13 0013
 */

public class ImageContainerLinearLayout extends LinearLayout {

    public ImageContainerLinearLayout(Context context) {
        this(context, null);
    }

    public ImageContainerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageContainerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setImages(final ArrayList<String> images) {
        post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
                if (images != null && !images.isEmpty()) {
                    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, getContext().getResources().getDisplayMetrics());
                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getContext().getResources().getDisplayMetrics());
                    int imageWidth = (getMeasuredWidth() - padding * 2) / 3;
                    int imageHeight = imageWidth - padding;
                    for (final String path : images) {
                        ImageView image = new ImageView(getContext());
                        LayoutParams params = new LayoutParams(imageWidth, imageHeight);
                        params.leftMargin = margin;
                        params.rightMargin = margin;
                        image.setLayoutParams(params);
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        GlideManager.loadRoundImage(getContext(), path, image,15);
//                        image.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                BrowserImageFragment.Companion.startBrowserImage(getContext(), images, images.indexOf(path));
//                            }
//                        });
                        addView(image);
                    }
                }
            }
        });
    }
}
