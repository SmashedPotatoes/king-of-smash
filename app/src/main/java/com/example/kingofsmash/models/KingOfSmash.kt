package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Dice

data class KingOfSmash(
    val players: List<Player>,
    var currentPlayerIdx: Int = 0,
    var playerInDFIdx: Int?,
    var currentAction: Action = Action.THROW_DICES,
    var cards: List<Card> = listOf(),
    var dices: List<Dice> = listOf(),
    )