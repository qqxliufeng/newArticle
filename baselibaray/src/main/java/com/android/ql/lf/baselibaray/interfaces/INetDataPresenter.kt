package com.android.ql.lf.baselibaray.interfaces

/**
 * Created by Administrator on 2017/10/17 0017.
 */
interface INetDataPresenter {
    fun onRequestStart(requestID: Int)

    fun onRequestFail(requestID: Int, e: Throwable)

    fun <T> onRequestSuccess(requestID: Int, result: T)

    fun onRequestEnd(requestID: Int)
}