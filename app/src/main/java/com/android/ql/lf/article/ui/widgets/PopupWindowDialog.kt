package com.android.ql.lf.article.ui.widgets

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import com.android.ql.lf.baselibaray.utils.hideSoftInput

/**
 * Created by lf on 2017/11/17 0017.
 * @author lf on 2017/11/17 0017
 */
class PopupWindowDialog {

    companion object {
        fun showReplyDialog(context: Context, contentView: View): PopupWindow {
            val popupWindow =
                PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.isTouchable = true
            popupWindow.setTouchInterceptor { v, event -> false }
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0)
//            val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            val lp = (context as Activity).window.attributes
            lp.alpha = 0.5f
            context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            context.window.attributes = lp
            popupWindow.setOnDismissListener {
                val lp2 = (context).window.attributes
                lp2.alpha = 1.0f
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                context.window.attributes = lp2
            }
            return popupWindow
        }

        fun toggleSoft(mContext:Context) {
            val im = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}