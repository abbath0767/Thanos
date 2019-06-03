package com.ng.thanoslayout.custom.stone

import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.ColorRes
import android.view.View
import com.ng.thanoslayout.custom.ThanosView

class TimeStone(@ColorRes color: Int) {
    val duration = 5000L

    fun destroyWithDelay(thanosViews: List<ThanosView>, viewsForDestroy: List<View>) {
        ViewDestroyer(thanosViews, viewsForDestroy).startDestroy()
    }

    private inner class ViewDestroyer(
        private val thanosViews: List<ThanosView>,
        private val viewsForDestroy: List<View>
    ) {

        private lateinit var iterator: ListIterator<ThanosView>

        fun startDestroy() {
            iterator = thanosViews.listIterator()
            process()
        }

        private fun process() {
            if (iterator.hasNext()) {
                val index = iterator.nextIndex()
                destroy(iterator.next(), viewsForDestroy[index])
            }
        }

        private fun destroy(thanosView: ThanosView, view: View) {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            thanosView.setBitmap(bitmap)
            thanosView.destroy { process() }
            view.visibility = View.INVISIBLE
        }
    }
}