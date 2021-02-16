package com.example.iamu.ui.main

import android.view.View
import android.view.animation.Animation

import android.view.animation.ScaleAnimation



class Utils {
    companion object {
        fun scaleZoomView(v: View, startScale: Float, endScale: Float, duration: Long, delay: Long) {
            val anim: Animation = ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.fillAfter = true
            anim.duration = duration
            anim.startOffset=delay
            v.startAnimation(anim)
        }
    }
}