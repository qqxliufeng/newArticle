package com.android.ql.lf.article.ui.fragments.article

import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.ARTICLENAV_ACT
import com.android.ql.lf.article.utils.ARTICLE_MODULE
import com.android.ql.lf.article.utils.getBaseParamsWithModAndAct
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import kotlinx.android.synthetic.main.fragment_article_list_layout.*
import org.jetbrains.anko.bundleOf

class ArticleListFragment : BaseNetWorkingFragment(){

    companion object {
        fun startTypeFragment(type:Int,list:ArrayList<Classify>) :ArticleListFragment{
            val articleListFragment = ArticleListFragment()
            articleListFragment.arguments = bundleOf(Pair("type",type), Pair("list",list))
            return articleListFragment
        }
    }

    private val list by lazy {
        (arguments?.get("list") ?: arrayListOf<Classify>()) as ArrayList<*>
    }

    override fun getLayoutId() = R.layout.fragment_article_list_layout

    override fun initView(view: View?) {
        mVpArticleList.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when(arguments?.getInt("type",0) ?: 0){
                    0-> ArticleListItemFragment.startArticleListItem((list[position] as Classify).classify_id,ArticleListItemFragment.INVALID_ID,ArticleListItemFragment.INVALID_ID)
                    1-> ArticleListItemFragment.startArticleListItem(ArticleListItemFragment.INVALID_ID,(list[position] as Classify).classify_id,ArticleListItemFragment.INVALID_ID)
                    2-> ArticleListItemFragment.startArticleListItem(ArticleListItemFragment.INVALID_ID,ArticleListItemFragment.INVALID_ID,(list[position] as Classify).classify_id)
                    else-> ArticleListItemFragment.startArticleListItem(ArticleListItemFragment.INVALID_ID,ArticleListItemFragment.INVALID_ID,ArticleListItemFragment.INVALID_ID)
                }
            }
            override fun getCount() = list.size

            override fun getPageTitle(position: Int) = (list[position] as Classify).classify_title
        }
        mVpArticleList.offscreenPageLimit = 4
        mTlArticleList.setupWithViewPager(mVpArticleList)
        mPresent.getDataByPost(0x0, getBaseParamsWithModAndAct(ARTICLE_MODULE, ARTICLENAV_ACT))
    }
}

class Classify(
    val classify_id: Int,
    val classify_title: String,
    var isChecked: Boolean = false,
    val classify_pic: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        1 == source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(classify_id)
        writeString(classify_title)
        writeInt((if (isChecked) 1 else 0))
        writeString(classify_pic)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Classify> = object : Parcelable.Creator<Classify> {
            override fun createFromParcel(source: Parcel): Classify = Classify(source)
            override fun newArray(size: Int): Array<Classify?> = arrayOfNulls(size)
        }
    }
}
