package com.android.ql.lf.article.data

/**
 * Created by lf on 18.11.17.
 * @author lf on 18.11.17
 */
class LeaveMessage {
    var leave_id : Int = 0
    var leave_uid : Int = 0
    var leave_content : String? = null
    var leave_rid : Int = 0
    var leave_cuid : Int = 0
    var leave_type : Int = 0
    var leave_theme : Int = 0
    var leave_times:String? = null
    var leave_userData : LeaveMessageUserData? = null
}

class LeaveMessageUserData{
    var member_id : Int = 0
    var member_phone :String? = null
    var member_pass :String? = null
    var member_nickname :String? = null
    var member_pic :String? = null
    var member_balance : Int = 0
    var member_fans : Int = 0
    var member_tags:String? = null
    var member_status : Int = 0
    var member_address : Int = 0
    var member_classify :String? = null
    var member_age : Int = 0
    var member_birthday :String? = null
    var member_sex : Int = 0
    var member_push : Int = 0
}