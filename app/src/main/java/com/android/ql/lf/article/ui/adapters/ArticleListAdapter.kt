package com.android.ql.lf.article.ui.adapters

import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.ArticleItem
import com.android.ql.lf.article.ui.widgets.ImageContainerLinearLayout
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ArticleListAdapter(list: ArrayList<ArticleItem>) : BaseMultiItemQuickAdapter<ArticleItem, BaseViewHolder>(list) {

    companion object {
        const val MULTI_IMAGE_TYPE = 0
        const val SINGLE_IMAGE_TYPE = 1
    }

    init {
        addItemType(MULTI_IMAGE_TYPE, R.layout.adapter_article_multi_image_type_item_layout)
        addItemType(SINGLE_IMAGE_TYPE, R.layout.adapter_article_single_image_type_item_layout)
    }


    override fun convert(helper: BaseViewHolder?, item: ArticleItem?) {
        when (item!!.mType) {
            MULTI_IMAGE_TYPE -> {
                helper?.setText(R.id.mTvArticleItemTitle,item.articles_title)
                helper?.setText(R.id.mTvArticleItemAuthName,item.articles_userData?.member_nickname)
                helper?.setText(R.id.mTvArticleItemCommentCount,"${item.articles_commentCount}")
                helper?.setText(R.id.mTvArticleItemLikeCount,"${item.articles_loveCount}")
                val balance = item.articles_balanceCount
                if (balance == 0){
                    helper?.setGone(R.id.mTvArticleItemPraiseCount,false)
                }else{
                    helper?.setGone(R.id.mTvArticleItemPraiseCount,true)
                }
                helper?.setText(R.id.mTvArticleItemPraiseCount,"${item.articles_balanceCount}")
                helper?.setText(R.id.mTvArticleItemType,item.articles_tags)
                val imageContainer = helper?.getView<ImageContainerLinearLayout>(R.id.mICllArticleListItemContainer)
                imageContainer?.setImages(item.articles_pic)
            }
            SINGLE_IMAGE_TYPE -> {
                helper?.setText(R.id.mTvArticleItemContent,item.articles_content)
                helper?.setText(R.id.mTvArticleItemTitle,item.articles_title)
                helper?.setText(R.id.mTvArticleItemAuthName,item.articles_userData?.member_nickname)
                helper?.setText(R.id.mTvArticleItemCommentCount,"${item.articles_commentCount}")
                helper?.setText(R.id.mTvArticleItemLikeCount,"${item.articles_loveCount}")
                val balance = item.articles_balanceCount
                if (balance == 0){
                    helper?.setGone(R.id.mTvArticleItemPraiseCount,false)
                }else{
                    helper?.setGone(R.id.mTvArticleItemPraiseCount,true)
                    helper?.setText(R.id.mTvArticleItemPraiseCount,"${item.articles_balanceCount}")
                }
                helper?.setText(R.id.mTvArticleItemType,item.articles_tags)
                if (item.articles_picCount == 0){
                    helper?.setGone(R.id.mIvArticleItemImage,false)
                }else {
                    helper?.setGone(R.id.mIvArticleItemImage,true)
                    if ( item.articles_pic!=null && !item.articles_pic!!.isEmpty()) {
                        GlideManager.loadRoundImage(
                            mContext,
                            item.articles_pic!![0],
                            helper?.getView(R.id.mIvArticleItemImage)
                        ,15)
                    }else{
                        helper?.setGone(R.id.mIvArticleItemImage,false)
                    }
                }
            }
        }
    }
}