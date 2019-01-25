package com.android.ql.lf.article.ui.fragments.other

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.android.ql.lf.article.R
import com.android.ql.lf.article.utils.OnImageClickListener
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity
import com.android.ql.lf.baselibaray.ui.fragment.BaseFragment
import com.android.ql.lf.baselibaray.utils.GlideManager
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.fragment_browser_image_layout.*
import org.jetbrains.anko.bundleOf

/**
 * Created by lf on 2017/12/21 0021.
 * @author lf on 2017/12/21 0021
 */
class BrowserImageFragment : BaseFragment(), ViewPager.OnPageChangeListener, OnImageClickListener {

    companion object {
        val IMAGE_LIST_FLAG = "image_list_flag"
        val CURRENT_IMAGE_FLAG = "current_image_flag"

        fun startBrowserImage(context: Context, list: ArrayList<String>, currentIndex: Int) {
            FragmentContainerActivity.startFragmentContainerActivity(context,
                    "",
                    false,
                    true,
                    bundleOf(Pair(IMAGE_LIST_FLAG, list),
                            Pair(CURRENT_IMAGE_FLAG, currentIndex)),
                    BrowserImageFragment::class.java)
        }
    }

    private val index by lazy {
        arguments!!.getInt(CURRENT_IMAGE_FLAG)
    }

    private val imageList by lazy {
        arguments!!.getStringArrayList(IMAGE_LIST_FLAG)
    }

    override fun getLayoutId() = R.layout.fragment_browser_image_layout

    override fun initView(view: View?) {
        val imageAdapter = ImageAdapter(imageList,this)
        mHVPImageContainer.adapter = imageAdapter
        mHVPImageContainer.currentItem = index
        mHVPImageContainer.addOnPageChangeListener(this)
        mTvBrowserImageIndex.text = "${index + 1}/${imageList.size}"
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        mTvBrowserImageIndex.text = "${position + 1}/${imageList.size}"
    }


    override fun onClick() {
        finish()
    }


    class ImageAdapter(private var imageList: ArrayList<String>, val imageClickListener: OnImageClickListener?) : PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any) = view == obj

        override fun getCount() =  imageList.size

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container!!.removeView(obj as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(container.context)
            photoView.setOnPhotoTapListener { view, x, y ->
                imageClickListener?.onClick()
            }
            GlideManager.loadImage(container.context, imageList[position], photoView)
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }
    }
}
