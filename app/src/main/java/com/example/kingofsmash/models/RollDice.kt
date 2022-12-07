package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Dice

data class RollDice(var dice: Dice = Dice.ONE, var shouldKeep: Boolean = false)
