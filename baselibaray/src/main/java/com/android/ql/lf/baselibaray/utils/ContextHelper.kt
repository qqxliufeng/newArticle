package com.android.ql.lf.baselibaray.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.windowManager
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.lang.IllegalArgumentException

fun Context.getScreen(): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
}

fun Fragment.getScreen() = context?.getScreen()

fun Context.showSoftInput(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

fun Context.hideSoftInput(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.compressAndSaveCacheFace(path: String, onCompressListener: OnCompressListener){
    context?.compressAndSaveCacheFace(path,onCompressListener)
}

fun Context.compressAndSaveCacheFace(path: String, onCompressListener: OnCompressListener) {
    if (path.isEmpty()) {
        throw IllegalArgumentException("path is null")
    }
    Luban.with(this).load(path).setTargetDir(cacheDir.absolutePath).setCompressListener(onCompressListener)
        .launch()
}