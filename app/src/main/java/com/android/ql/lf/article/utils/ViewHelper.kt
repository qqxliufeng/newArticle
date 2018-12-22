package com.android.ql.lf.article.utils

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.data.isLogin
import com.android.ql.lf.article.ui.fragments.login.LoginFragment
import org.jetbrains.anko.alert
import java.util.regex.Pattern

val PHONE_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}\$"
val IDCARD_REG = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)\$"
val MONEY_REG = "^(([1-9]\\d*)|([0]))(\\.(\\d){0,2})?\$"
val INTEGER_REG = "^[1-9]\\d*\$"

fun Float.toDip(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

/**
 * 显示SnackBar
 */
fun View.showSnackBar(message: String) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    snackBar.view.setBackgroundResource(R.color.colorPrimary)
    snackBar.show()
}

/**
 * 校验输入框是否为空
 */
fun EditText.isEmpty(): Boolean {
    return TextUtils.isEmpty(this.text.toString().trim())
}

fun EditText.isNotMoney(): Boolean {
    return !Pattern.compile(MONEY_REG).matcher(this.text).matches()
}

fun EditText.isNotPhone(): Boolean {
    return !Pattern.compile(PHONE_REG).matcher(this.text).matches()
}

fun EditText.isInteger() : Boolean{
    return Pattern.compile(INTEGER_REG).matcher(this.text).matches()
}

fun String.isInteger():Boolean{
    return Pattern.compile(INTEGER_REG).matcher(this).matches()
}

fun EditText.passwordIsNotInRange(startIndex: Int = 6, endInt: Int = 16): Boolean {
    return getTextString().length > endInt || getTextString().length < startIndex
}


fun String.isPhone(): Boolean {
    return Pattern.compile(PHONE_REG).matcher(this).matches()
}

fun EditText.isIdCard(): Boolean {
    return Pattern.compile(IDCARD_REG).matcher(this.text).matches()
}

fun String.isIdCard(): Boolean {
    return Pattern.compile(IDCARD_REG).matcher(this).matches()
}

fun EditText.getTextString(): String {
    return this.text.toString().trim()
}

fun EditText.setFirstPoint() {
    if (this.text.startsWith(".")) {
        setText("0.")
        this.setSelection(this.text.toString().length)
    }
}


fun EditText.setTextChangedListener(afterTextWatcher: (Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextWatcher(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}


fun EditText.getFormateFloat(): String? {
    try {
        if (this.isEmpty()) {
            return null
        }
        return java.lang.Float.parseFloat(getTextString()).toString()
    } catch (e: Exception) {
        return null
    }
}

fun TextView.setDiffColorText(source1: String, source2: String, color1: String = "#c8c9ca", color2: String = "#545557") {
    text = Html.fromHtml(source1.fromHtml(color1) + source2.fromHtml(color2))
}


fun String.fromHtml(color: String = "#c8c9ca"): String {
    return "<font color='$color'>$this</font>"
}


fun Fragment.alert(title: String? = "title",
                   message: String = "message",
                   positiveButton: String = "是",
                   negativeButton: String = "否",
                   positiveAction: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
                   negativeAction: ((dialog: DialogInterface, which: Int) -> Unit)? = null) =
        this.context?.alert(title, message, positiveButton, negativeButton, positiveAction, negativeAction)

fun Fragment.alert(message: String = "message",
                   positiveButton: String = "是",
                   negativeButton: String = "否", positiveAction: ((dialog: DialogInterface, which: Int) -> Unit)? = null) =
        this.alert(null, message, positiveButton, negativeButton, positiveAction, null)

fun Context.alert(title: String? = "title",
                  message: String = "message",
                  positiveButton: String = "是",
                  negativeButton: String = "否",
                  positiveAction: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
                  negativeAction: ((dialog: DialogInterface, which: Int) -> Unit)? = null) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setTitle(title)
    builder.setNegativeButton(negativeButton, negativeAction)
    builder.setPositiveButton(positiveButton, positiveAction)
    builder.create().show()
}


fun View.doClickWithUserStatusStart(token: String, action: (view: View) -> Unit) {
    setOnClickListener {
        if (UserInfo.isLogin()) {
            action(this)
        } else {
//            UserInfo.loginToken = token
            LoginFragment.startLoginFragment(this.context)
        }
    }
}

//fun View.doClickWithUseStatusEnd() {
//    performClick()
//    UserInfo.resetLoginSuccessDoActionToken()
//}

