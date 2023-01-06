package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Dice

data class KingOfSmash(
    val players: List<Player>,
    var currentPlayerIdx: Int = 0,
    var playerInDF: Player?,
    var currentAction: Action = Action.THROW_DICES,
    var cards: MutableList<Card> = mutableListOf(),
    var cardsInDeck: MutableList<Card> = mutableListOf(),
    var selectedCard: Card? = null,
    var dices: List<Dice> = listOf(),
    var rank: Int = 4,
)