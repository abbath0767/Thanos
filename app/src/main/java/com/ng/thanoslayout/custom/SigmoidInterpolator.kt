package com.ng.thanoslayout.custom

import android.animation.TimeInterpolator

@Deprecated("Useless")
class SigmoidInterpolator : TimeInterpolator {

    private val c = 0.5
    private val a = 5.0

    override fun getInterpolation(input: Float) = (1f / (1f + Math.pow(Math.E, -a * (input - c)))).toFloat()
}