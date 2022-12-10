package com.example.kingofsmash.models

import android.widget.ImageView
import android.widget.TextView

data class GameOverPlayerCard (
    val id: Int,
    val backgroundImage: ImageView,
    val name: TextView,
    val stock: TextView,
    val smashMeter: TextView,
    val game: TextView,
    val kills: List<ImageView>
)