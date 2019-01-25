package com.android.ql.lf.article.data

import android.arch.lifecycle.LiveData
import com.android.ql.lf.article.application.MyApplication
import com.android.ql.lf.article.ui.fragments.bottom.MineFragment
import com.android.ql.lf.baselibaray.utils.PreferenceUtils
import com.android.ql.lf.baselibaray.utils.RxBus
import org.json.JSONObject

const val USER_ID_FLAG = "user_id"

/**
 * 广播用户数据
 */
fun UserInfo.postUserInfo() = UserInfoLiveData.postUserInfo()

/**
 * 重新请求用信息广播
 */
fun UserInfo.reloadUserInfo(){
    RxBus.getDefault().post(MineFragment.UPDATE_USER_INFO_FLAG)
}

/**
 * 更新用户数据
 */
fun UserInfo.updateUserInfo(nickName:String, userPic:String){
    user_nickname = nickName
    user_pic = userPic
    this.postUserInfo()
}

/**
 * 清除用户数据
 */
fun UserInfo.clearUserInfo() {
    user_id = -1
    user_phone = null
    user_nickname = null
    user_pic = null
    this.postUserInfo()
}

/**
 * 判断用户是否是登录状态
 */
fun UserInfo.isLogin(): Boolean = user_id != -1 && user_phone != null


fun UserInfo.loginOut(){
    user_id = -1
    user_phone = null
    PreferenceUtils.setPrefInt(MyApplication.getInstance(), USER_ID_FLAG, user_id)
    postUserInfo()
}


/**
 * 解析用户数据
 */
fun UserInfo.jsonToUserInfo(json: JSONObject): Boolean {
    return try {
        user_id = json.optInt("member_id")
        if (user_id == -1){
            return false
        }
        user_nickname = json.optString("member_nickname")
        user_phone = json.optString("member_phone")
        if (user_phone == null){
            return false
        }
        user_pass = json.optString("member_pass")
        user_pic = json.optString("member_pic")
        user_balance = json.optInt("member_balance")
        user_fans = json.optInt("member_fans")
        user_tags = json.optString("member_tags")
        user_status = json.optInt("member_status")
        user_address = json.optInt("member_address")
        user_classify = json.optString("member_classify")
        user_age = json.optInt("member_age")
        user_birthday = json.optString("member_birthday")
        user_sex = json.optInt("member_sex")
        user_push = json.optInt("member_push")
        user_loveCount = json.optInt("member_loveCount")
        user_likeCount = json.optInt("member_likeCount")
        user_fanCount  = json.optInt("member_fanCount")
        user_colStatus = json.optInt("member_colStatus")
        user_signature = json.optString("member_signature")
        user_cover = json.optString("member_cover")
        user_front = json.optString("member_front")
        user_reverse = json.optString("member_reverse")
        user_leave = json.optString("member_leave")
        user_allStatus = json.optInt("member_allStatus")
        user_likeStatus = json.optInt("member_likeStatus")

        user_qq = json.optString("member_qq")
        user_wx = json.optString("member_wx")
        user_weibo = json.optString("member_weibo")
        PreferenceUtils.setPrefInt(MyApplication.getInstance(), USER_ID_FLAG, user_id)
        true
    } catch (e: Exception) {
        false
    }
}

object UserInfo {
    var user_id: Int = -1
    var user_phone: String? = null
    var user_pass: String? = null
    var user_nickname: String? = null
    var user_pic: String? = null
    var user_balance:Int? = null
    var user_fans:Int? = null
    var user_tags:String? = null
    var user_status:Int = 1
    var user_address:Int? = null
    var user_classify:String? = null
    var user_age:Int = 1
    var user_birthday:String? = null
    var user_sex:Int = 0
    var user_push:Int? = null
    var user_signature:String? = null
    var user_loveCount:Int = 0
    var user_likeCount:Int = 0
    var user_fanCount:Int  = 0
    var user_colStatus:Int = 0
    var user_cover:String? = null
    var user_front:String? = null
    var user_reverse:String? = null
    var user_leave:String? = null
    var user_allStatus:Int = 0
    var user_likeStatus:Int = 0
    var user_qq:String? = null
    var user_wx:String? = null
    var user_weibo:String? = null
}

enum class AuthStatus(flag:Int){
//    1 未认证  2 待审核  3 已认证 4 认证失败
    NO_AUTH(1),
    WAIT_AUTH(2),
    COMPLEMENT_AUTH(3),
    FAIL_AUTH(4);

    companion object {
        fun getNameByFlag(flag: Int):AuthStatus{
            return when(flag){
                1-> NO_AUTH
                2-> WAIT_AUTH
                3-> COMPLEMENT_AUTH
                4-> FAIL_AUTH
                else-> NO_AUTH
            }
        }
    }

}

object UserInfoLiveData : LiveData<UserInfo>() {

    fun postUserInfo() {
        postValue(UserInfo)
//        value = UserInfo
    }

}
