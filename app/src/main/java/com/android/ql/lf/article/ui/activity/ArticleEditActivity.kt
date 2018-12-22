package com.android.ql.lf.article.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import com.android.ql.lf.article.R
import com.android.ql.lf.article.ui.fragments.article.ArticleEditFragment
import com.android.ql.lf.article.ui.fragments.article.ArticleUpdateFragment
import com.android.ql.lf.article.ui.fragments.article.Classify
import com.android.ql.lf.article.utils.ARTICLE_EDIT_ACT
import com.android.ql.lf.article.utils.ARTICLE_ISSUE_ACT
import com.android.ql.lf.baselibaray.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_article_edit_layout.*
import org.jetbrains.anko.bundleOf
import org.jsoup.Jsoup

class ArticleEditActivity : BaseActivity() {

    companion object {
        fun startArticleEditActivity(context: Context,title:String,content: String, types:Classify,mode:Int = 0,aid:Int = 0) {
            val intent = Intent(context, ArticleEditActivity::class.java)
            intent.putExtra("title",title)
            intent.putExtra("content", content)
            intent.putExtra("types",types)
            intent.putExtra("mode",mode)
            intent.putExtra("aid",aid)
            context.startActivity(intent)
        }
    }

    private val types : Classify by lazy {
        intent.getParcelableExtra("types") as Classify
    }

    private val mode by lazy {
        intent.getIntExtra("mode",0)
    }

    override fun getLayoutId() = R.layout.activity_article_edit_layout

    private val articleEditFragment by lazy {
        val articleEditFragment = ArticleEditFragment()
        articleEditFragment.arguments = bundleOf(
            Pair("title", intent.getStringExtra("title")),
            Pair("types",types)
        )
        articleEditFragment
    }

    private val articleUpdateFragment by lazy {
        val articleUpdateFragment = ArticleUpdateFragment()
        articleUpdateFragment.arguments = bundleOf(
            Pair("aid",intent.getIntExtra("aid",0)),
            Pair("content", intent.getStringExtra("content")),
            Pair("title", intent.getStringExtra("title"))
        )
        articleUpdateFragment
    }

    private val content by lazy {
        intent.getStringExtra("content") ?: ""
    }

    override fun initView() {
        statusBarColor = Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setSupportActionBar(mTlArticleEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mTlArticleEdit.setNavigationOnClickListener { onBackPressed() }
        if (mode == 0) {
            mTlArticleEdit.title = "编辑文章"
            mTvArticleEditMenu.text = "发布"
            mTvArticleEditMenu.setOnClickListener {
                articleEditFragment.publicArticle(ARTICLE_ISSUE_ACT)
            }
            supportFragmentManager.beginTransaction().replace(R.id.mFlArticleEditContainer, articleEditFragment)
                .commit()
        }else{
            mTlArticleEdit.title = "更新文章"
            mTvArticleEditMenu.text = "更新发布"
            mTvArticleEditMenu.setOnClickListener {
                articleUpdateFragment.publicArticle(ARTICLE_EDIT_ACT)
            }
            if (!TextUtils.isEmpty(content)){
                val length = Jsoup.parse(content).body().text().length
                setSubTitleText(length)
                supportFragmentManager.beginTransaction().replace(R.id.mFlArticleEditContainer, articleUpdateFragment)
                    .commit()
            }
        }
    }

    fun setSubTitleText(subText: Int) {
        mTlArticleEdit.subtitle = "${subText}字"
    }

    override fun onBackPressed() {
        if (mode == 0) {
            articleEditFragment.onBackPress()
        }else{
            articleUpdateFragment.onBackPress()
        }
    }

}