package com.android.ql.lf.article.data

/**
 * Created by lf on 18.11.16.
 * @author lf on 18.11.16
 */
class ArticleCommentItem {
    var comment_id: Int? = null
    var comment_uid: Int? = null
    var comment_content: String? = null
    var comment_times: String? = null
    var comment_theme: Int? = null
    var comment_rid: Int? = null
    var comment_husername: String? = null
    var comment_f: Int? = null
    var comment_type: Int? = null
    var comment_like: Int? = null
    var comment_userData: ArticleCommentUserData? = null
    var comment_reply: ArrayList<ArticleCommentReply>? = arrayListOf()
    var comment_replyNum:Int = 0
    var isPriase :Boolean = false
}

class ArticleCommentUserData(val member_nickname: String = "", val member_pic: String = "")

class ArticleCommentReply {
    var comment_id: Int? = null
    var comment_uid: Int? = null
    var comment_content: String? = null
    var comment_times: String? = null
    var comment_theme: Int? = null
    var comment_rid: Int? = null
    var comment_husername: String? = null
    var comment_f: Int? = null
    var comment_type: Int? = null
    var comment_userData: ArticleCommentUserData? = null
}