package com.ng.thanoslayout.custom

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class Thanos {

    private val infinityFist = InfinityFist()

    private var viewsForDestroy: List<View> = emptyList()
    private var thanosViews: List<ThanosView> = emptyList()

    fun snap(container: ViewGroup) {
        fillListViewsForSnap(container)
        fillListThanosViews(container)
        startDestroy()
    }

    private fun fillListViewsForSnap(container: ViewGroup) {
        val childCount = container.childCount
        val result = mutableListOf<View>().apply {
            val chaosValue = infinityFist.realityStone.getChaoticValue()
            for (index in 0 until childCount) {
                if ((index + chaosValue) % 2 == 0)
                    add(container.getChildAt(index))
            }
        }

        viewsForDestroy = result.toList()
    }

    private fun fillListThanosViews(container: ViewGroup) {
        val gaussianInterpolator = GaussianInterpolator(
            infinityFist.soulStone.sigma,
            infinityFist.soulStone.mu,
            infinityFist.soulStone.multipier
        )

        val thanosViews = mutableListOf<ThanosView>().apply {
            viewsForDestroy.forEach { viewForDestroy ->
                val thanosView = ThanosView(container.context).apply {
                    infinityFist = this@Thanos.infinityFist
                    interpolator = gaussianInterpolator
                    squareSize = infinityFist.spaceStone.squareSize
                    alphaPaint.alpha = infinityFist.realityStone.alphaStart
                    id = View.generateViewId()
                }
                val layoutParams = RelativeLayout.LayoutParams(
                    viewForDestroy.width * 2,
                    viewForDestroy.height * 2
                ).apply {
                    addRule(RelativeLayout.ALIGN_RIGHT, viewForDestroy.id)
                    addRule(RelativeLayout.ALIGN_LEFT, viewForDestroy.id)
                    addRule(RelativeLayout.ALIGN_TOP, viewForDestroy.id)
                    addRule(RelativeLayout.ALIGN_BOTTOM, viewForDestroy.id)
                    setMargins(
                        -viewForDestroy.width / 2,
                        -viewForDestroy.height / 2,
                        -viewForDestroy.width / 2,
                        -viewForDestroy.height / 2
                    )
                }
                add(thanosView)
                container.addView(thanosView, layoutParams)
            }
        }


        this.thanosViews = thanosViews.toList()
    }

    private fun startDestroy() {
        infinityFist.timeStone.destroyWithDelay(thanosViews, viewsForDestroy)
    }
}