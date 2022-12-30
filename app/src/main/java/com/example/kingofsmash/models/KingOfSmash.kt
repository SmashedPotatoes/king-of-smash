package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Dice

data class KingOfSmash(
    val players: List<Player>,
    var currentPlayerIdx: Int = 0,
    var playerInDF: Player?,
    var currentAction: Action = Action.THROW_DICES,
    var cards: List<Card> = listOf(),
    var cardsInDeck: List<Card> = listOf(),
    var dices: List<Dice> = listOf(),
    var rank: Int = 4,
    ) {
    init {
        if (playerInDF == null) {
            playerInDF = players.first()
        }
    }
}