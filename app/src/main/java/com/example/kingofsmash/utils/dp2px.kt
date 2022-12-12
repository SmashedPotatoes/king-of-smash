package com.example.kingofsmash.utils

import android.content.res.Resources

fun dp2px(dp: Int): Int {
    val scale =  Resources.getSystem().displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}