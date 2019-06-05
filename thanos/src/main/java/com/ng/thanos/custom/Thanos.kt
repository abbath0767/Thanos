package com.ng.thanos.custom

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


class Thanos(private val activity: Activity) {

    private val infinityFist = InfinityFist()

    private var viewsForDestroy: List<View> = emptyList()
    private var thanosViews: List<ThanosView> = emptyList()
    private var screenWidth = 0
    private var screenHeight = 0

    fun snap() {
        fillListViewsForSnap()
        fillListThanosViews()
        startDestroy()
    }

    private fun fillListViewsForSnap() {
        val root = getRoot()
        val chaosValue = infinityFist.realityStone.getChaoticValue()
        val result = mutableListOf<View>()
        ViewTraverser { view, index ->
            if ((index + chaosValue) % 2 == 0)
                result.add(view)
        }.traverse(root)

        viewsForDestroy = result.toList()
    }

    private fun fillListThanosViews() {
        val gaussianInterpolator = GaussianInterpolator(
            infinityFist.soulStone.sigma,
            infinityFist.soulStone.mu,
            infinityFist.soulStone.multipier
        )

        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        val thanosViews = mutableListOf<ThanosView>().apply {
            viewsForDestroy.forEach { view ->
                val location = IntArray(2).apply {
                    view.getLocationInWindow(this)
                }

                val thanosView = generateThanosView(gaussianInterpolator)
                val params = generateLayoutParams(view, location)
                    .apply {
                        gravity = Gravity.TOP or Gravity.START

                        thanosView.let {
                            when {
                                x < 0 && y < 0 -> {
                                    it.leftBorder = location[0]
                                    it.topBorder = location[1]
                                }
                                x < 0 -> {
                                    it.leftBorder = location[0]
                                    it.topBorder = view.height / 2
                                }
                                y < 0 -> {
                                    it.leftBorder = view.width / 2
                                    it.topBorder = location[1]
                                }
                                (x + view.width * 2 > screenWidth) && (y + view.height * 2 > screenHeight) -> {
                                    it.leftBorder = (location[0] + view.width * 2) - screenWidth
                                    it.topBorder = (location[1] + view.height * 2) - screenHeight
                                }
                                x + view.width * 2 > screenWidth -> {
                                    it.leftBorder = (location[0] + view.width * 2) - screenWidth
                                    it.topBorder = view.height / 2
                                }
                                y + view.height * 2 > screenHeight -> {
                                    it.leftBorder = view.width / 2
                                    it.topBorder = (location[1] + view.height * 2) - screenHeight
                                }
                                else -> {
                                    it.leftBorder = view.width / 2
                                    it.topBorder = view.height / 2
                                }
                            }
                        }
                    }

                wm.addView(thanosView, params)
                add(thanosView)
            }
        }

        this.thanosViews = thanosViews.toList()
    }

    private fun generateThanosView(gaussianInterpolator: GaussianInterpolator) =
        ThanosView(activity.baseContext).apply {
            infinityFist = this@Thanos.infinityFist
            interpolator = gaussianInterpolator
            squareSize = infinityFist.spaceStone.squareSize
            alphaPaint.alpha = infinityFist.realityStone.alphaStart
            id = View.generateViewId()
        }

    private fun generateLayoutParams(view: View, location: IntArray) = WindowManager.LayoutParams(
        view.width * 2,
        view.height * 2,
        location[0] - view.width / 2,
        location[1] - view.height / 2,
        WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSPARENT
    )

    private fun getRoot() =
        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup

    private fun startDestroy() {
        infinityFist.timeStone.destroyWithDelay(thanosViews, viewsForDestroy)
    }

    internal class ViewTraverser(private val marker: (View, Int) -> Unit) {
        private var counter = 0
        internal fun traverse(viewGroup: ViewGroup) {
            val childCount = viewGroup.childCount
            for (index in 0 until childCount) {
                val view = viewGroup.getChildAt(index)
                if (view is ViewGroup) {
                    traverse(view)
                } else if (view.width != 0 && view.height != 0 && view.visibility == View.VISIBLE) {
                    marker.invoke(view, counter)
                    counter++
                }
            }
        }
    }
}