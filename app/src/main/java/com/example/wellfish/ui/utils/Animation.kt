package com.example.wellfish.ui.utils

import android.app.Activity
import android.content.Context
import com.example.wellfish.R

object Animation {
    @Suppress("DEPRECATION")
    fun animateSlideLeft(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.animate_slide_left_enter,
            R.anim.animate_slide_left_exit
        )
    }
}