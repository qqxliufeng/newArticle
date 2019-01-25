package com.android.ql.lf.article.ui.fragments.other

import android.content.Context
import android.util.TypedValue
import android.view.View
import com.android.ql.lf.article.utils.MEMBER_MODULE
import com.android.ql.lf.article.utils.SYSTEM_PROTOCOL_ACT
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import kotlinx.android.synthetic.main.fragment_article_web_view_layout.*
import org.json.JSONObject

class SystemNotifyFragment : NetWebViewFragment() {

    companion object {
        fun startSystemNotifyFragment(context:Context){
            FragmentContainerActivity.from(context).setTitle("系统公告").setNeedNetWorking(true).setClazz(SystemNotifyFragment::class.java).start()
        }
    }

    override fun initView(view: View?) {
        super.initView(view)
        mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(MEMBER_MODULE,SYSTEM_PROTOCOL_ACT))
        mWVArticleWebViewContainer.settings.defaultFontSize  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12.0f,this.resources.displayMetrics).toInt()
    }


    override fun onRequestStart(requestID: Int) {
        if(requestID == 0x0){
            getFastProgressDialog("正在加载……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        super.onRequestSuccess(requestID, result)
        val check = checkResultCode(result)
        if (check!=null){
            if(check.code == SUCCESS_CODE){
                mWVArticleWebViewContainer.loadData((check.obj as JSONObject).optString(RESULT_OBJECT),"text/html;charset=utf-8",null)
            }
        }
    }
}