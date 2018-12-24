package com.android.ql.lf.article.ui.widgets

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleCommentReply
import com.android.ql.lf.article.data.LeaveMessageReplyData
import com.android.ql.lf.article.utils.toDip

class LeaveReplyLinearLayout : LinearLayoutCompat {

    private var onSeeMore: ((Boolean) -> Unit)? = null

    private val padding = 10.0f.toDip(context).toInt()

    private val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#2d8acb"))

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#f5f6fa"))
    }


    fun setData(list: ArrayList<LeaveMessageReplyData>?,seeMore:Boolean = false) {
        if (list == null || list.isEmpty()) {
            visibility = View.GONE
            return
        }
        post {
            visibility = View.VISIBLE
            removeAllViews()
            if (list.size > 4) {
                if (!seeMore) {
                    (0 until 4).forEach {
                        val nickName = list[it].member_nickname
                        addView(getTextView("$nickName 回复：${list[it].leave_content}", nickName.length + 1))
                    }
                    val reply = "展开更多 > "
                    val textView = getTextView(reply, reply.length)
                    textView.textSize = 14.0f
                    textView.setOnClickListener {
                        removeAllViews()
                        list.forEach {
                            val nickName = it.member_nickname
                            addView(getTextView("$nickName 回复：${it.leave_content}", nickName.length + 1))
                        }
                        this.onSeeMore?.invoke(!seeMore)
                    }
                    addView(textView)
                }else{
                    list.forEach {
                        val nickName = it.member_nickname
                        addView(getTextView("$nickName 回复：${it.leave_content}", nickName.length + 1))
                    }
                }
            } else {
                list.forEach {
                    val nickName = it.member_nickname
                    addView(getTextView("$nickName 回复：${it.leave_content}", nickName.length + 1))
                }
            }
        }
    }

    fun setOnSeeMore(onSeeMore: ((Boolean) -> Unit)?){
        this.onSeeMore = onSeeMore
    }

    private fun getTextView(string: String, end: Int): TextView {
        val textView = TextView(context)
        textView.setTextColor(ContextCompat.getColor(context, R.color.normalTextColor))
        textView.textSize = 14.0f
        textView.text = getWrapperText(string, end)
        textView.setPadding(padding, padding / 2, padding, padding / 5)
        textView.setLineSpacing(1.0f,1.2f)
        return textView
    }

    private fun getWrapperText(string: String, end: Int): SpannableString {
        val spannableString = SpannableString(string)
        spannableString.setSpan(foregroundColorSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}