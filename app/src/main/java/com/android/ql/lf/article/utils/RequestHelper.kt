package com.android.ql.lf.article.utils

import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.isLogin
import com.android.ql.lf.baselibaray.component.ApiParams

/**
 * Created by lf on 18.11.13.
 * @author lf on 18.11.13
 */

fun getBaseParams(): ApiParams = ApiParams().addParam("pt", "android").addParam("uid", if (UserInfo.isLogin()) UserInfo.user_id else "")

fun getBaseParamsWithModAndAct(mod: String, act: String) : ApiParams = getBaseParams().addParam(ApiParams.MOD_NAME, mod).addParam(ApiParams.ACT_NAME, act)

fun getBaseParamsWithPage(mod: String, act: String, page: Int = 0, pageSize: Int = 10) : ApiParams = getBaseParamsWithModAndAct(mod, act).addParam("page", page).addParam("pagesize", pageSize)

/**       公用模块                **/
const val T_MODULE = "t"
const val UPLOADING_PIC_ACT = "uploadingPic"
const val WX_LOGIN_ACT = "wxlogin"

/**       登录模块                **/
const val LOGIN_MODULE = "login"

const val LOGINDO_ACT = "loginDo"
const val REGISTERDO_ACT = "registerDo"
const val SMSCODE_ACT = "smscode"
const val ADDRESS_ACT = "address"
const val CLASSIFY_ACT = "classify"
const val EDITUSERPIC_ACT = "editUserPic"
const val WEIBO_LOGIN_ACT = "weiboLogin"
const val QQ_LOGIN_ACT = "qqLogin"
const val PERFECT_ACT = "perfect"
const val RESET_PASSWORD_ACT = "resetPassword"

/**       文章模块                **/
const val ARTICLE_MODULE = "article"
const val ARTICLENAV_ACT = "articleNav"
const val ARTICLE_LIST_ACT = "articleList"
const val ARTICLE_REUSER_ACT = "articleReuser"
const val ARTICLE_DETAIL_ACT = "articleDetail"
const val ARTICLE_LIKE_ACT = "articleLike"
const val ARTICLE_ISSUE_ACT = "articleIssue"
const val ARTICLE_COMMENT_ACT = "articleComment"
const val ARTICLE_COMMENT_DO_ACT = "articleCommentDo" //发表评论
const val ARTICLE_COMMENT_REPLY_ACT = "articleCommentReply" //评论回复
const val ARTICLE_COMMENT_LIKE_ACT = "articleCommentLike" //评论喜欢
const val ARTICLE_LOVE_ACT = "articleLove" //文章喜欢
const val ARTICLE_COLLECT_ACT = "articleCollect" //文章收藏
const val ARTICLE_STATUS_ACT = "articleStatus" //文章状态修改
const val ARTICLE_DEL_STATUS_ACT = "articleDelStatus" //文章删除
const val ARTICLE_EDIT_ACT = "articleEdit" //文章编辑
const val ARTICLE_ADMIRE_ACT = "articleAdmire" //文章赞赏

/**       用户模块                **/
const val MEMBER_MODULE = "member"
const val PERSONAL_ACT = "personal"
const val EDIT_USER_PIC_ACT = "editUserPic"
const val PERSONAL_EDIT_ACT = "personalEdit"
const val MY_MAIN_ACT = "myMain"
const val COVER_PIC_ACT = "coverPic"
const val MY_LIKE_DO_ACT = "myLikeDo"
const val ARTICLE_DEL_ACT = "articleDel" //文章真正删除
const val IDEA_DO_ACT = "ideaDo"
const val PERSONAL_CERT_ACT = "personalCert"
const val ACCOUNT_SAFE_ACT = "accountSafe"
const val MY_LIKE_FRIEND_ACT = "myLikeFriend"
const val SETTING_ACT = "setting"

/**       消息模块                **/
const val MESSAGE_MODULE = "message"
const val MESSAGE_ACT = "message"
const val PUSH_ACT = "push"
const val LEAVE_DO_ACT = "LeaveDo"
const val MY_LEAVE_DETAIL_ACT = "myLeaveDetail"
const val COMMENT_REPLY_ACT = "commentReply"
const val LEAVE_SHARE_DO_ACT = "LeaveShareDo"