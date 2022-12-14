package com.example.kingofsmash.models

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView

data class PlayerCard(
    val id: Int,
    val icon: ImageView,
    val name: TextView,
    val stock: TextView,
    val smashMeter: TextView,
    val game: TextView,
    val background: GradientDrawable,
    val crown: ImageView,
    val view: View,
    val actionText: TextView,
    val actionIcon: ImageView
) {
}