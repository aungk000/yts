package me.ako.yts.presentation.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import me.ako.yts.R

class PageIndicator : LinearLayout {

    private var childViewsCount = 0
    private var childViews: Array<ImageView?>? = null
    var iconActive: Drawable? = null
    var iconInactive: Drawable? = null
    private var viewPager2: ViewPager2? = null

    constructor(context: Context) : super(context) {
        setIconDrawables(R.drawable.dot_active, R.drawable.dot_inactive)
        setPadding(0, 32, 0, 32)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setIconDrawables(R.drawable.dot_active, R.drawable.dot_inactive)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    constructor(
        context: Context,
        @DrawableRes iconActive: Int,
        @DrawableRes iconInactive: Int
    ) : super(context) {
        setIconDrawables(iconActive, iconInactive)
        this.setPadding(0, 32, 0, 32)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    constructor(
        context: Context,
        iconActive: Drawable,
        iconInactive: Drawable
    ) : super(context) {
        setIconDrawables(iconActive, iconInactive)
        this.setPadding(0, 32, 0, 32)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    private fun setIconDrawables(@DrawableRes iconActive: Int, @DrawableRes iconInactive: Int) {
        this.iconActive = AppCompatResources.getDrawable(context, iconActive)
        this.iconInactive = AppCompatResources.getDrawable(context, iconInactive)
    }

    private fun setIconDrawables(iconActive: Drawable, iconInactive: Drawable) {
        this.iconActive = iconActive
        this.iconInactive = iconInactive
    }

    private fun setChildViews(count: Int) {
        childViews = arrayOfNulls(count)
        for (i in 0 until count) {
            childViews!![i] = ImageView(context)
            if (i == 0) {
                childViews!![i]!!.setImageDrawable(iconActive)
            } else {
                childViews!![i]!!.setImageDrawable(iconInactive)
            }
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 0, 10, 0)
            addView(childViews!![i], params)
        }
    }

    fun setupWithViewPager(viewPager2: ViewPager2) {
        this.viewPager2 = viewPager2

        if (viewPager2.adapter != null) {
            childViewsCount = viewPager2.adapter!!.itemCount
            if(childViews.isNullOrEmpty()) {
                setChildViews(childViewsCount)
            }
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectIndicator(position)
            }
        })
    }

    private fun selectIndicator(position: Int) {
        for (i in 0 until childViewsCount) {
            if (i == position) {
                childViews?.get(i)?.setImageDrawable(iconActive)
            } else {
                childViews?.get(i)?.setImageDrawable(iconInactive)
            }
        }
    }
}