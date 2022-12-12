package com.example.kingofsmash.utils

import android.widget.Button
import com.example.kingofsmash.R
import com.example.kingofsmash.enums.Dice

fun initDieButton(button: Button, die: Dice) {
    button.text = ""
    button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

    when (die) {
        Dice.ONE, Dice.TWO, Dice.THREE -> button.setPadding(
            button.paddingLeft,
            dp2px(6),
            button.paddingRight,
            dp2px(8)
        )
        else -> button.setPadding(button.paddingLeft, dp2px(12), button.paddingRight, dp2px(10))
    }

    when (die) {
        Dice.ONE -> button.text = "1"
        Dice.TWO -> button.text = "2"
        Dice.THREE -> button.text = "3"
        Dice.SMASH_METER -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            R.drawable.icon_smash_meter_dice,
            0,
            0
        )
        Dice.SMASH -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            R.drawable.icon_smash,
            0,
            0
        )
        Dice.STOCK -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            R.drawable.icon_stock, 0, 0
        )
    }
}
