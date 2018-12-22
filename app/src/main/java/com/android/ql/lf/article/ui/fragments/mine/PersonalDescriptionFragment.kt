package com.android.ql.lf.article.ui.fragments.mine

import android.content.Context
import android.support.v4.view.MenuItemCompat
import android.view.*
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.postUserInfo
import com.android.ql.lf.article.ui.widgets.SingleTextViewActionProvide
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.fragment_personal_description_layout.*
import org.jetbrains.anko.support.v4.toast

class PersonalDescriptionFragment : BaseNetWorkingFragment() {

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_personal_description_layout

    override fun initView(view: View?) {
        mEtPersonalDescriptionContent.isLongClickable = false

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.submit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val submitMenu = menu?.findItem(R.id.mMenuSubmit)
        val provider = MenuItemCompat.getActionProvider(submitMenu) as SingleTextViewActionProvide
        provider.setOnActionClick {
            if (mEtPersonalDescriptionContent.isEmpty()){
                toast("请输入内容")
                return@setOnActionClick
            }
            mPresent.getDataByPost(0x2, getBaseParamsWithModAndAct(MEMBER_MODULE, PERSONAL_EDIT_ACT)
                .addParam("signature",mEtPersonalDescriptionContent.getTextString()))
        }
        provider.setTitle("确定")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onRequestStart(requestID: Int) {
        getFastProgressDialog("正在保存……")
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        val check = checkResultCode(result)
        if (check!=null && check.code == SUCCESS_CODE){
            toast("签名修改成功")
            UserInfo.user_signature = mEtPersonalDescriptionContent.getTextString()
            UserInfo.postUserInfo()
            finish()
        }else{
            toast("签名修改失败")
        }
    }

}
