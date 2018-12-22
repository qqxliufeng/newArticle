package com.android.ql.lf.article.data

import com.chad.library.adapter.base.entity.MultiItemEntity

class ArticleItem : MultiItemEntity {

    var mType: Int = 0

    override fun getItemType() = mType

    var articles_id:Int? = null
    var articles_title:String? = null
    var articles_address:Int? = null
    var articles_tags:String? = null
    var articles_uid:Int? = null
    var articles_content:String? = null
    var articles_rec:Int? = null
    var articles_numcount:Int? = null
    var articles_read:Int? = null
    var articles_privacy:Int? = null
    var articles_like:Int = 0
    var articles_collect:Int = 0
    var articles_comment:Int? = null
    var articles_status:Int = 0
    var articles_age:Int? = null
    var articles_pic:ArrayList<String>? = null
    var articles_picCount:Int = 0
    var articles_times:String? = null
    var articles_userData:ArticleUserDataBean? = null
    var articles_love:Int = 0


    var articles_desc:String? = null
    var articles_loveCount:Int? = null
    var articles_commentCount:Int = 0
    var articles_balanceCount:Int = 0

    var articles_shareUrl:String? = null
}

data class ArticleUserDataBean( var member_id:Int,var member_nickname:String,var member_pic:String)

enum class ArticleType(val type: Int) {
    //文章状态  1 公开  2 私密  3 收录  4 投稿  5  回收站  6 喜欢  7 收藏  8 浏览历史
    PRIVATE_ARTICLE(2),
    PUBLIC_ARTICLE(1),
    COLLECTION_ARTICLE(3),
    POST_ARTICLE(4),
    TRASH_ARTICLE(5),
    LOVE_ARTICLE(6),
    MY_COLLECTION_ARTICLE(7),
    HISTORY_ARTICLE(8),
    OTHER(-1);

    companion object {
        fun getTypeNameById(type: Int): ArticleType {
            return when (type) {
                PRIVATE_ARTICLE.type -> {
                    PRIVATE_ARTICLE
                }
                PUBLIC_ARTICLE.type -> {
                    PUBLIC_ARTICLE
                }
                COLLECTION_ARTICLE.type -> {
                    COLLECTION_ARTICLE
                }
                POST_ARTICLE.type -> {
                    POST_ARTICLE
                }
                TRASH_ARTICLE.type->{
                    TRASH_ARTICLE
                }
                LOVE_ARTICLE.type->{
                    LOVE_ARTICLE
                }
                MY_COLLECTION_ARTICLE.type->{
                    MY_COLLECTION_ARTICLE
                }
                HISTORY_ARTICLE.type->{
                    HISTORY_ARTICLE
                }
                else -> {
                    POST_ARTICLE
                }
            }
        }
    }
}