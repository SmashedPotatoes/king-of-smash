package com.example.kingofsmash.models

import android.widget.ImageView
import android.widget.TextView

data class PlayerCard(
    val id: Int,
    val icon: ImageView,
    val name: TextView,
    val stock: TextView,
    val smashMeter: TextView,
    val game: TextView,
) {
}