package com.ng.thanoslayout.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

class ThanosLayout : RelativeLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val infinityFist = InfinityFist()

    private var viewsForDestroy: List<View> = emptyList()
    private var thanosViews: List<ThanosView> = emptyList()

    fun snap() {
        fillListViewsForSnap()
        fillListThanosViews()
        startDestroy()
    }

    private fun fillListViewsForSnap() {
        val childCount = childCount
        val result = mutableListOf<View>().apply {
            val chaosValue = infinityFist.realityStone.getChaoticValue()
            for (index in 0 until childCount) {
                if ((index + chaosValue) % 2 == 0)
                    add(getChildAt(index))
            }
        }

        viewsForDestroy = result.toList()
    }

    //todo add view to window/root
    private fun fillListThanosViews() {
        val thanosViews = mutableListOf<ThanosView>().apply {
            viewsForDestroy.forEach { viewForDestroy ->
                val thanosView = ThanosView(context).apply {
                    infinityFist = this@ThanosLayout.infinityFist
                    id = View.generateViewId()
                }
//                val layoutParams = viewForDestroy.layoutParams
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
                addView(thanosView, layoutParams)
            }
        }


        this.thanosViews = thanosViews.toList()
    }

    private fun startDestroy() {
        infinityFist.timeStone.destroyWithDelay(thanosViews, viewsForDestroy)
    }
}
