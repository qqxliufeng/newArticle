package com.android.ql.lf.article.utils

import android.content.Context

/**
 * Created by lf on 18.11.26.
 * @author lf on 18.11.26
 */
fun Context.currentVersion() = packageManager.getPackageInfo(packageName,0).versionCode
