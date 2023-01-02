package com.example.kingofsmash.utils

import com.example.kingofsmash.enums.CardType
import com.example.kingofsmash.models.Card

fun initAllCards() : MutableList<Card>{
    val cards = mutableListOf<Card>()

    cards.add(Card(1, "Heal 1 heart", CardType.HEAL_ONE))
    cards.add(Card(2, "Heal 2 heart", CardType.HEAl_TWO))

    cards.add(Card(1, "Dead 1 damage to 1 random enemy", CardType.DAMAGE_RANDOM_ONE))
    cards.add(Card(2, "Dead 2 damage to 1 random enemy", CardType.DAMAGE_RANDOM_TWO))
    cards.add(Card(3, "Dead 3 damage to 1 random enemy", CardType.DAMAGE_RANDOM_THREE))

    cards.add(Card(3, "Gain 1 game", CardType.GAME_UP_ONE))
    cards.add(Card(6, "Gain 2 games", CardType.GAME_UP_TWO))

    cards.add(Card(6, "Have a 25% chance to kill a random enemy", CardType.RANDOM_KILL_ONE))
    cards.add(Card(9, "Gain 55% chance to kill a random enemy", CardType.RANDOM_KILL_TWO))

    return cards
}