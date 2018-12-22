package com.android.ql.lf.article.ui.fragments.mine

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.getTextString
import com.android.ql.lf.article.utils.isEmpty
import kotlinx.android.synthetic.main.dialog_personal_edit_nick_name_layout.*
import org.jetbrains.anko.support.v4.toast

class PersonalEditNickNameDialogFragment : DialogFragment() {

    private var callback : ((String)->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_personal_edit_nick_name_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBtPersonalEditNickNameDialogSubmit.setOnClickListener {
            if (mEtPersonalEditNickNameDialog.isEmpty()) {
                toast("请输入昵称")
                return@setOnClickListener
            }
            this.callback?.invoke(mEtPersonalEditNickNameDialog.getTextString())
            dismiss()
        }
        mBtPersonalEditNickNameDialogCancel.setOnClickListener { dismiss() }
    }

    fun myShow(fragmentManager: FragmentManager,tag:String,callback:(String)->Unit){
        show(fragmentManager,tag)
        this.callback = callback
    }
}