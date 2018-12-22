package com.android.ql.lf.article.ui.fragments.article

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.View
import android.webkit.JavascriptInterface
import com.android.ql.lf.article.R
import com.android.ql.lf.article.data.UserInfo
import com.android.ql.lf.article.ui.activity.ArticleEditActivity
import com.android.ql.lf.article.ui.fragments.mine.IdentityAuthFragment
import com.android.ql.lf.article.utils.*
import com.android.ql.lf.baselibaray.data.ImageBean
import com.android.ql.lf.baselibaray.ui.fragment.BaseNetWorkingFragment
import com.android.ql.lf.baselibaray.utils.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_article_edit_layout.*
import okhttp3.MultipartBody
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import org.jsoup.Jsoup
import top.zibin.luban.OnCompressListener
import java.io.File

open class ArticleEditFragment : BaseNetWorkingFragment() {

    companion object {
        const val EDITOR_HTML_FLAG = "editor_html"
    }

    private var isBoldChecked: Boolean = false

    protected val selectedImages = arrayListOf<SelectedImageBean>()

    protected var selectedImageBean : SelectedImageBean? = null

    protected var articleCount:Int = 0

    protected var tempHtml:String = ""

    protected var aid:Int = 0

    private val classify by lazy {
        arguments?.classLoader = this.javaClass.classLoader
        arguments?.getParcelable<Classify>("types")
    }

    protected val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    protected var act:String = ""

    private var uploadStatus:Int = 1

    override fun getLayoutId() = R.layout.fragment_article_edit_layout

    override fun initView(view: View?) {
        mEtArticleEditTitle.setText(arguments?.getString("title","")?:"")
        mEtArticleEditTitle.requestFocus()
        mIBArticleEditActionImage.isEnabled =false
        mIBArticleEditActionBold.isEnabled = false
        mIBArticleEditActionLink.isEnabled = false
        setBoldImage()
        mIBArticleEditActionImage.setColorFilter(Color.parseColor("#cdcbcb"))
        mIBArticleEditActionBold.setColorFilter(Color.parseColor("#cdcbcb"))
        mIBArticleEditActionLink.setColorFilter(Color.parseColor("#cdcbcb"))
        mReArticleEdit.setPlaceholder("请输入内容")
        mReArticleEdit.setPadding(0, 10, 0, 10)
        mReArticleEdit.setTextColor(ContextCompat.getColor(mContext, R.color.normalTextColor))
//        mReArticleEdit.setOnInitialLoadListener {
////                mReArticleEdit.focusEditor()
//            mEtArticleEditTitle.isEnabled = true
//            mIBArticleEditActionImage.isEnabled = true
//            mIBArticleEditActionBold.isEnabled = true
//            mIBArticleEditActionLink.isEnabled = true
//            mReArticleEdit.setInputEnabled(true)
//        }
        mReArticleEdit.addJavascriptInterface(MyEditorJavascriptInterface(),"aApi")
        mEtArticleEditTitle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                mIBArticleEditActionImage.isEnabled = !hasFocus
                mIBArticleEditActionBold.isEnabled = !hasFocus
                mIBArticleEditActionLink.isEnabled = !hasFocus
                mIBArticleEditActionImage.setColorFilter(Color.parseColor("#cdcbcb"))
                mIBArticleEditActionBold.setColorFilter(Color.parseColor("#cdcbcb"))
                mIBArticleEditActionLink.setColorFilter(Color.parseColor("#cdcbcb"))
            }
        }

        mReArticleEdit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                mIBArticleEditActionImage.isEnabled = hasFocus
                mIBArticleEditActionBold.isEnabled = hasFocus
                mIBArticleEditActionLink.isEnabled = hasFocus
                setBoldImage()
                mIBArticleEditActionImage.setColorFilter(Color.parseColor("#888888"))
                mIBArticleEditActionLink.setColorFilter(Color.parseColor("#888888"))
            }
        }

        mIBArticleEditActionImage.setOnClickListener {
            isBoldChecked = false
            setBoldImage()
            openImageChoose(MimeType.ofImage(), 1)
        }

        mIBArticleEditActionBold.setOnClickListener {
            isBoldChecked = !isBoldChecked
            mReArticleEdit.setBold()
            setBoldImage()
        }

        mIBArticleEditActionLink.setOnClickListener {
            isBoldChecked = false
            setBoldImage()
            val dialog = ArticleEditAddLinkDialogFragment()
            dialog.show(childFragmentManager, "link")
        }

        mReArticleEdit.setOnTextChangeListener {
            val length = Jsoup.parse(it).body().text().length
            articleCount = length
            (mContext as ArticleEditActivity).setSubTitleText(length)
            tempHtml = it
        }
    }

    fun publicArticle(act:String){
        this.act = act
        uploadStatus = 1
        if (!selectedImages.isEmpty()){
            selectedImages.forEach {
                if (it.httpPath == null || it.httpPath == ""){
                    toast("上传失败，请重新编辑")
                    return@forEach
                }
            }
        }
        if (mEtArticleEditTitle.isEmpty()){
            toast("请输入文章标题")
            return
        }
        if (articleCount == 0 && selectedImages.isEmpty()){
            toast("请输入文章内容")
            return
        }
        replaceImagePath()
    }

    private fun replaceImagePath() {
        exec(
            """javascript:(function(){
                            var images = document.getElementsByTagName("img")
                            for (var i = 0; i < images.length; i++) {
                                var image = images[i]
                                if(image.src.indexOf("file://") == 0){
                                    var httpPath = aApi.getHttpPathByJSPath(image.src)
                                    image.src = httpPath
                                }
                            }
                            aApi.postUpload(document.getElementsByTagName('body')[0].innerHTML)
                        }())
                """
        )
    }

    open fun upload(html: String) {
        val param = getBaseParamsWithModAndAct(ARTICLE_MODULE, this.act)
            .addParam("title", mEtArticleEditTitle.getTextString())
            .addParam("content", html.replace("""id="editor" contenteditable="true" placeholder="请输入内容" style="padding: 10px 0px;"""",""))
            .addParam("count", articleCount)
            .addParam("status",uploadStatus)
        if (this.act == ARTICLE_EDIT_ACT){
            param.addParam("aid",aid)
        }else{
//            param.addParam("age", UserInfo.user_age)
//            param.addParam("address", UserInfo.user_address)
            param.addParam("classify", classify?.classify_id ?: 0)
        }
        mPresent.getDataByPost(
            0x1, param
        )
    }

    private fun privateArticle(act: String){
        this.act = act
        uploadStatus = 2
        if (articleCount == 0 && selectedImages.isEmpty()){
            toast("文章取消")
            finish()
            return
        }//
        replaceImagePath()
    }

    private fun exec(js: String = "") {
        val clazz = RichEditor::class.java
        val method = clazz.getDeclaredMethod("exec", String::class.java)
        method.isAccessible = true
        method.invoke(mReArticleEdit, js)
    }

    private fun setBoldImage() {
        if (isBoldChecked) {
            mIBArticleEditActionBold.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent))
        } else {
            mIBArticleEditActionBold.setColorFilter(Color.parseColor("#888888"))
        }
    }

    fun addLink(linkBean: LinkBean) {
        mReArticleEdit.insertLink(linkBean.address, linkBean.name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x0 && resultCode == Activity.RESULT_OK && data != null && mReArticleEdit.isFocusable) {
            compressAndSaveCacheFace(Matisse.obtainPathResult(data)[0], object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    selectedImageBean = SelectedImageBean(file?.absolutePath ?: "", null)
                    selectedImages.add(selectedImageBean!!)
                    mReArticleEdit.insertImage(selectedImageBean!!.srcPath, """img "style="width:100%;padding:15px 0px 15px 0px""")
                    ImageUploadHelper(object : ImageUploadHelper.OnImageUploadListener {
                        override fun onActionStart() {
                            getFastProgressDialog("正在上传图片……")
                        }

                        override fun onActionEnd(builder: MultipartBody.Builder?) {
                            mPresent.uploadFile(0x0, T_MODULE, UPLOADING_PIC_ACT, builder?.build()?.parts())
                        }

                        override fun onActionFailed() {
                        }

                    }).upload(arrayListOf(ImageBean(null, file?.absolutePath ?: "")))
                }

                override fun onError(e: Throwable?) {
                }

                override fun onStart() {
                }
            })
        }
    }

    override fun onRequestStart(requestID: Int) {
        super.onRequestStart(requestID)
        if (requestID == 0x1){
            getFastProgressDialog("正在上传文章……")
        }
    }

    override fun <T : Any?> onRequestSuccess(requestID: Int, result: T) {
        if (requestID == 0x0) {
            val json = JSONObject(result as String)
            val code = json.optInt("code")
            if (code == 200) {
                toast("图片上传成功")
                selectedImageBean?.httpPath = GlideManager.getImageUrl(json.optString("result"))
            } else {
                toast("图片上传失败")
            }
        }else if (requestID == 0x1){
            val check = checkResultCode(result)
            if (check!=null) {
                when {
                    check.code == SUCCESS_CODE -> {
                        uploadSuccess()
                        toast("上传成功")
                        finish()
                    }
                    check.code == "400" -> {
                        toast("请先进行身份认证")
                        IdentityAuthFragment.startIdentityAuthFragment(mContext)
                    }
                    check.code == "500" -> {
                        toast("当前身份信息正在认证中，暂不能公开发布文章")
                    }
                }
            }else{
                toast("上传失败")
            }
        }
    }

    open fun uploadSuccess(){

    }

    open fun onBackPress() {
        privateArticle(ARTICLE_ISSUE_ACT)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    inner class MyEditorJavascriptInterface{

        /**
         * 根据js传过来的本地image.src查询对应上传网络后得到的图片地址，进行拼装后上传到服务器
         */
        @JavascriptInterface
        public fun getHttpPathByJSPath(jsPath:String) : String{
            for (imageBean in selectedImages){
                if ("file://${imageBean.srcPath}" == jsPath)
                    return imageBean.httpPath ?: jsPath
            }
            return jsPath
        }

        @JavascriptInterface
        public fun postUpload(html: String){
            handler.post {
                upload(html)
            }
        }
    }
}

data class LinkBean(val name: String, val address: String)

data class SelectedImageBean(val srcPath: String? = "", var httpPath: String? = "")
