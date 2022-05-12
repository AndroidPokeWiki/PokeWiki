package com.example.pokewiki.utils

import android.content.Context

fun dip2px(context: Context, dpValue: Double): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}