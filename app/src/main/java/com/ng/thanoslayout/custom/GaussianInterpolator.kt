package com.ng.thanoslayout.custom

import android.animation.TimeInterpolator

//todo add normal javadoc, please
class GaussianInterpolator(
    private val sigma: Float = 1f,
    private val u: Float = 1f,
    private val multipier: Float = 1f
) : TimeInterpolator {

    // * multipier - is a costyl (gaussian/normal probability destiny do non pervenire to value == 1)
    override fun getInterpolation(input: Float): Float {
        return (((1f / (sigma * Math.sqrt(2 * Math.PI))) * Math.pow(
            Math.E,
            -0.5 * Math.pow(((input - u) / sigma).toDouble(), 2.0)
        )) / 4).toFloat() * multipier
    }
}