package com.shorbgy.tiktokclone.extensions

import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.LinearInterpolator

fun View.fadeInOut(fadeInDuration: Long = 500L, fadeOutDuration: Long = 300L, interpolator: TimeInterpolator? = null) {
    alpha = 0.0f
    animate()
        .alpha(1.0f)
        .setDuration(fadeInDuration)
        .setInterpolator(interpolator ?: LinearInterpolator())
        .withEndAction {
            animate()
                .alpha(0.0f)
                .setDuration(fadeOutDuration)
                .setInterpolator(interpolator ?: LinearInterpolator())
                .start()
        }
        .start()
}