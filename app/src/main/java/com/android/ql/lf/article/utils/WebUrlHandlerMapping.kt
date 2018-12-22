package com.android.ql.lf.article.utils

import android.annotation.SuppressLint
import android.util.TypedValue
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.ql.lf.baselibaray.utils.BaseConfig

const val JS_BRIDGE_INTERFACE_NAME: String = "article"

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setNormalSetting() {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.useWideViewPort = true
//    settings.defaultFontSize  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12.0f,this.resources.displayMetrics).toInt()
    settings.loadWithOverviewMode = true
    settings.allowFileAccess = true
    settings.databaseEnabled = true
    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
    this.clearCache(true)
}

fun WebView.loadLocalHtml(url: String = "") {
    loadUrl("file:///android_asset/$url")
//    this.loadUrl("${BaseConfig.BASE_IP}article/$url")
}

fun WebView.loadWrapperData(data: String?) {
    this.loadData(
        """<div style="font-size: 2.8rem;">$data</div>
                           <script type="text/javascript">
                                (function(){
                                    var aobjs = document.getElementsByTagName("a");
                                    for (var i = 0; i < aobjs.length; i++) {
                                        var a = aobjs[i];
                                        a.style = "text-decoration: none; color : blue"
                                    }
                                    var objs = document.getElementsByTagName('img');
                                    for(var i=0;i<objs.length;i++){
                                        var img = objs[i];
                                        img.style.width = '100%'; img.style.height = 'auto';
                                    }
                                }())
                           </script>
                        """, "text/html;charset=UTF-8", null
    )
}