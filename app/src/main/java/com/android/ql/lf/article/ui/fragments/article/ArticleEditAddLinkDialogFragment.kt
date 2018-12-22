package com.android.ql.lf.article.ui.fragments.article

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.getTextString
import com.android.ql.lf.article.utils.isEmpty
import org.jetbrains.anko.support.v4.toast

class ArticleEditAddLinkDialogFragment : DialogFragment() {

    private var mEtLinkName: EditText? = null
    private var mEtLinkAddress: EditText? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_link_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEtLinkName = view.findViewById(R.id.mEtAddLinkDialogName)
        mEtLinkAddress = view.findViewById(R.id.mEtAddLinkDialogAddress)
        view.findViewById<Button>(R.id.mBtAddLinkDialogCancel).setOnClickListener { dismiss() }
        view.findViewById<Button>(R.id.mBtAddLinkDialogSubmit).setOnClickListener {
            if (mEtLinkName!!.isEmpty()) {
                toast("请输入网址名称")
                return@setOnClickListener
            }
            if (mEtLinkAddress!!.isEmpty()) {
                toast("请输入网址地址")
                return@setOnClickListener
            }
            dismiss()
            val address = mEtLinkAddress?.getTextString()
            if (address!=null){
                if (address.startsWith("http://") || address.startsWith("https://")){
                    (parentFragment as ArticleEditFragment).addLink(LinkBean(mEtLinkName?.getTextString()
                        ?: "", mEtLinkAddress?.getTextString() ?: ""))
                }else{
                    (parentFragment as ArticleEditFragment).addLink(LinkBean(mEtLinkName?.getTextString()
                        ?: "", "http://$address"))
                }
            }
        }
    }
}