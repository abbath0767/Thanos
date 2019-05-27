package com.ng.thanoslayout.custom.stone

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.support.annotation.ColorRes
import android.view.View
import com.ng.thanoslayout.custom.ThanosView
import java.util.*

class TimeStone(@ColorRes color: Int) {
    private val handler = Handler()
    private val random = Random()

    private val destroyDelay
        get() = (random.nextDouble() * 500L + 500L).toLong()

    val duration = 5000L

    fun destroyWithDelay(thanosViews: List<ThanosView>, viewsForDestroy: List<View>) {
        viewsForDestroy.forEachIndexed { index, destroyView ->
            handler.postDelayed({
                val bitmap = Bitmap.createBitmap(destroyView.width, destroyView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                destroyView.draw(canvas)
                thanosViews[index].setBitmap(bitmap)
                destroyView.visibility = View.INVISIBLE
                thanosViews[index].destroy()
            }, destroyDelay)
        }
    }
}