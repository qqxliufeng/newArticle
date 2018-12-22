package com.android.ql.lf.article.ui.fragments.article

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.activity.ArticleEditActivity
import com.android.ql.lf.article.utils.CLASSIFY_ACT
import com.android.ql.lf.article.utils.LOGIN_MODULE
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseRecyclerViewFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_select_type_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.toast

class SelectTypeFragment : BaseRecyclerViewFragment<Classify>() {

    companion object {
        fun startSelectTypeFragment(context: Context, title: String) {
            FragmentContainerActivity.from(context).setExtraBundle(bundleOf(Pair("title", title))).setHiddenToolBar(true).setNeedNetWorking(true).setClazz(SelectTypeFragment::class.java).start()
        }
    }

    private var mCurrentClassify : Classify? = null

    override fun getLayoutId() = R.layout.fragment_select_type_layout

    override fun createAdapter(): BaseQuickAdapter<Classify, BaseViewHolder> = object : BaseQuickAdapter<Classify, BaseViewHolder>(R.layout.adapter_select_type_item_layout, mArrayList) {
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
        (mTvSelectTypeFirstStep.parent as ConstraintLayout).setPadding(0,statusBarHeight,0,0)
        mTvSelectTypeFirstStep.setOnClickListener { finish() }
        mTvSelectTypeNextStep.setOnClickListener {
            if (mCurrentClassify == null){
                toast("请选择一个文章类别")
                return@setOnClickListener
            }
            ArticleEditActivity.startArticleEditActivity(mContext,"","",mCurrentClassify!!)
            finish()
        }
        mTvSelectTypeTitle.text = arguments?.getString("title", "") ?: ""
        setLoadEnable(false)
        setRefreshEnable(false)
    }

    override fun onRefresh() {
        super.onRefresh()
        mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(LOGIN_MODULE,CLASSIFY_ACT))
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0){
            processList(result as String,Classify::class.java)
        }
    }

    override fun onMyItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onMyItemClick(adapter, view, position)
        mArrayList.forEach {
            it.isChecked = false
        }
        mArrayList[position].isChecked = true
        mCurrentClassify = mArrayList[position]
        mBaseAdapter.notifyDataSetChanged()
    }
}