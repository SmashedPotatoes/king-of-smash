package com.example.kingofsmash.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Dice : Parcelable {
    ONE,
    TWO,
    THREE,
    SMASH_METER,
    SMASH,
    STOCK,
}