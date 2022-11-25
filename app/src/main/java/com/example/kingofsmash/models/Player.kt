package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.PlayerType

data class Player(
    val type: PlayerType,
    val character: Character,
    var stock: Int = 10,
    var smashMeter: Int = 0,
    var game: Int = 0,
    var isAlive: Boolean = true,
)