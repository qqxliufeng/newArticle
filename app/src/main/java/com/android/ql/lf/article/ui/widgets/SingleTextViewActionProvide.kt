package com.android.ql.lf.article.ui.widgets

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.ActionProvider
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.ql.lf.article.R


class SingleTextViewActionProvide(context: Context) : ActionProvider(context) {

    private var textView: TextView? = null
    private var onActionClick: (() -> Unit)? = null
    private var title: String = ""
    private var textColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var isEnable: Boolean = true

    override fun onCreateActionView(): View {
        textView = TextView(context)
        textView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textView!!.setTextColor(textColor)
        textView!!.gravity = Gravity.CENTER
        textView!!.setPadding(0, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, context.resources.displayMetrics).toInt(), 0)
        textView!!.text = title
        textView!!.isEnabled = isEnable
        textView!!.textSize = 16.0f
        textView!!.setOnClickListener {
            onActionClick?.invoke()
        }
        return textView!!
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setOnActionClick(click: (() -> Unit)? = null) {
        onActionClick = click
    }

    fun setTextColor(color: Int) {
        this.textColor = color
    }

    fun isEnable(isEnable: Boolean) {
        this.isEnable = isEnable
    }

}