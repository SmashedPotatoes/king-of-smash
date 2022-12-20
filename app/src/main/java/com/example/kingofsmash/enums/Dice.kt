package com.example.kingofsmash.enums

import android.os.Parcelable
import com.example.kingofsmash.models.RollDice
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.random.Random

@Parcelize
enum class Dice : Parcelable {
    ONE,
    TWO,
    THREE,
    SMASH_METER,
    SMASH,
    STOCK;

    companion object {
        fun getRandom(random: Random): Dice {
            return values()[random.nextInt(0, 6)]
        }
    }

    fun toRollDice() = RollDice(this)
}