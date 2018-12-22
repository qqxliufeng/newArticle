package com.android.ql.lf.article.ui.fragments.article

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.EditText
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.getTextString
import com.android.ql.lf.article.utils.isEmpty
import com.android.ql.lf.article.utils.isInteger
import com.android.ql.lf.baselibaray.utils.GlideManager
import kotlinx.android.synthetic.main.dialog_article_admire_layout.*
import org.jetbrains.anko.support.v4.toast

class ArticleAdmireDialogFragment : DialogFragment() {

    private val countList = listOf(2, 5, 10, 20, 50)
    private val checkedTextViewList = arrayListOf<CheckedTextView>()
    private var editText:EditText? = null

    private var facePath:String = ""

    private var price:Int = 0

    private var callback:((content:String,price:Int)->Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_article_admire_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlideManager.loadFaceCircleImage(context,facePath,mIvArticleAdmireUserInfoFace)
        mIvArticleAdmireClose.setOnClickListener { dismiss() }
        mBtArticleAdmireUserAdmire.setOnClickListener {
            if (price == 0){
                toast("请选择赞赏金额")
                return@setOnClickListener
            }
            this.callback?.invoke(if (mEtArticleAdmireLeaveMessage.isEmpty()){""}else{mEtArticleAdmireLeaveMessage.getTextString()},price)
            dismiss()
        }
        checkedTextViewList.clear()
        (0 until mClArticleAdmireTangContainer.childCount).forEach {
            val child = mClArticleAdmireTangContainer.getChildAt(it)
            if (child is CheckedTextView) {
                checkedTextViewList.add(child)
                child.text = getCheckViewText(countList[it])
                child.setOnClickListener{ ch ->
                    checkedTextViewList.forEach {item->
                        item.isChecked = item == ch
                        if(item.isChecked){
                            price = countList[mClArticleAdmireTangContainer.indexOfChild(item)]
                            mTvArticleAdmireCount.text = "${price}￥"
                        }
                    }
                    editText?.clearFocus()
                }
            }else{
                editText = child as EditText
                editText?.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus){
                        mTvArticleAdmireCount.text = "￥0"
                        checkedTextViewList.forEach { item2-> item2.isChecked = false }
                    }
                }
                editText?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s!=null && !s.isNullOrEmpty()){
                            if (s.toString().isInteger()){
                                price = s.toString().toInt()
                                mTvArticleAdmireCount.text = "￥$s"
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
        }
    }

    fun setFacePath(facePath:String){
        this.facePath = facePath
    }

    fun setCallBack(callback:(content:String,price:Int)->Unit){
        this.callback = callback
    }

    private fun getCheckViewText(count: Int): SpannableString {
        val text = "$count 颗"
        val spannableString = SpannableString(text)
        spannableString.setSpan(AbsoluteSizeSpan(10, true), text.lastIndex, text.lastIndex+1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    override fun onDestroyView() {
        checkedTextViewList.clear()
        super.onDestroyView()
    }

}