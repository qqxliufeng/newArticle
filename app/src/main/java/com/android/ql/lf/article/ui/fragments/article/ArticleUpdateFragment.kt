package com.android.ql.lf.article.ui.fragments.article

import android.view.View
import com.android.ql.lf.article.data.ArticleItem
import com.android.ql.lf.article.utils.alert
import com.android.ql.lf.baselibaray.utils.RxBus
import kotlinx.android.synthetic.main.fragment_article_edit_layout.*

class ArticleUpdateFragment : ArticleEditFragment() {

    override fun initView(view: View?) {
        super.initView(view)
        aid = arguments?.getInt("aid") ?: 0
        mEtArticleEditTitle.setText(arguments?.getString("title","")?:"")
        mReArticleEdit.focusEditor()
        mReArticleEdit.setOnInitialLoadListener {
            val html = arguments?.getString("content", "") ?: ""
            if (html != "") {
                mReArticleEdit.html = html
            }
        }
    }

    override fun uploadSuccess() {
        RxBus.getDefault().post(ArticleInfoForNormalFragment.UPDATE_ARTICLE_FLAG)
        RxBus.getDefault().post(ArticleItem())
    }

    override fun onBackPress() {
        alert("要否已完成编辑？","是","否"){_,_->
            finish()
        }
    }
}
