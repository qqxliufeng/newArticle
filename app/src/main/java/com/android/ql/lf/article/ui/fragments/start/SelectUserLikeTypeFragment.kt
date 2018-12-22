package com.android.ql.lf.article.ui.fragments.start

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.activity.MainActivity
import com.android.ql.lf.article.ui.fragments.article.Classify
import com.android.ql.lf.article.utils.CLASSIFY_ACT
import com.android.ql.lf.article.utils.LOGIN_MODULE
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_select_user_like_type_layout.*
import kotlinx.android.synthetic.main.layout_pre_step.*
import org.jetbrains.anko.support.v4.toast
import java.lang.StringBuilder
import java.util.ArrayList

class SelectUserLikeTypeFragment : BaseRecyclerViewFragment<Classify>() {

    private val selectItemsList = arrayListOf<Classify>()

    override fun createAdapter(): BaseQuickAdapter<Classify, BaseViewHolder> =
        object : BaseQuickAdapter<Classify, BaseViewHolder>(R.layout.adapter_select_type_item_layout, mArrayList) {
            override fun convert(helper: BaseViewHolder?, item: Classify?) {
                helper?.setText(R.id.mTvSelectTypeItemName,item?.classify_title)
                GlideManager.loadCircleImage(mContext,item?.classify_pic,helper?.getView(R.id.mIvSelectTypeItemPic))
                if (item!!.isChecked){
                    helper?.setImageResource(R.id.mIvSelectTypeItemCheck,R.drawable.img_select_icon)
                }else{
                    helper?.setImageResource(R.id.mIvSelectTypeItemCheck,R.drawable.shape_un_select_bg)
                }
            }
        }

    override fun getLayoutId() = R.layout.fragment_select_user_like_type_layout

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(mContext, 3)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        val itemDecoration = super.getItemDecoration() as DividerItemDecoration
        itemDecoration.setDrawable(ColorDrawable(Color.WHITE))
        return itemDecoration
    }

    override fun initView(view: View?) {
        super.initView(view)
        setLoadEnable(false)
        setRefreshEnable(false)
        mTvPreFirstStep.setOnClickListener {
            (parentFragment as StartCustomTypeFragment).positionFragment(0)
        }
        mTvSelectTypeNextStep.setOnClickListener {
            toast("正在为您生成主页")
            if (!selectItemsList.isEmpty()) {
                val stringBuilder = StringBuilder()
                selectItemsList.forEach {
                    stringBuilder.append(it.classify_id)
                    stringBuilder.append(",")
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndex)
                PreferenceUtils.setPrefString(mContext,"my_classify",stringBuilder.toString())
            }
            mTvSelectTypeNextStep.postDelayed({
                startActivity(Intent(mContext, MainActivity::class.java))
                finish()
            },1500)
        }
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(LOGIN_MODULE, CLASSIFY_ACT))
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            processList(result as String,Classify::class.java)
        }
    }

    override fun actionTempList(tempList: ArrayList<Classify>?) {
        tempList?.forEach { it.isChecked = false }
    }

    override fun onMyItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemClick(adapter, view, position)
        val item = mArrayList[position]
        if (item.isChecked){
            item.isChecked = false
            if (selectItemsList.contains(item)) {
                selectItemsList.remove(item)
            }
        }else{
            if (selectItemsList.size == 3){
                toast("最多只能选择三种类别")
            }else {
                item.isChecked = true
                if (!selectItemsList.contains(item)) {
                    selectItemsList.add(item)
                }
            }
        }
        mBaseAdapter.notifyItemChanged(position)
    }
}