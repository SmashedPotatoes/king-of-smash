package com.example.kingofsmash.viewmodels

import androidx.lifecycle.ViewModel
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.enums.PlayerType
import com.example.kingofsmash.models.KingOfSmash
import com.example.kingofsmash.models.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KingOfSmashViewModel(character: Character) : ViewModel() {
    private val state = MutableStateFlow(
        KingOfSmash(
            Character.values().map { Player(if (character == it) PlayerType.PLAYER else PlayerType.BOT, character) },
            0,
            null
        )
    )
    val stateFlow = state.asStateFlow()

    fun executeDices(dices: List<Dice>) {
        println("executeDices")
        println(dices)
        state.value = state.value.copy(
            currentAction = Action.EXECUTE_DICES,
            dices = dices
        )
    }

    fun endTurn() {
        state.value = state.value.copy(
            currentPlayerIdx = (state.value.currentPlayerIdx + 1) % 4,
            currentAction = Action.THROW_DICES
        )
    }
}