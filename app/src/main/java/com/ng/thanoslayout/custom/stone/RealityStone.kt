package com.ng.thanoslayout.custom.stone

import android.support.annotation.ColorRes
import java.util.*

class RealityStone(@ColorRes color: Int) {

    fun getChaoticValue() = Random().nextInt(2)

    val alphaStart = 255
    val alphaEnd = 0
}