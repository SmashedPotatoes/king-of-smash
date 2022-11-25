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

    fun getCurrentPlayer(): Player = state.value.players[state.value.currentPlayerIdx]
    fun throwDices(dices: List<Dice>) {
        state.value = state.value.copy(
            currentAction = Action.EXECUTE_DICES,
            dices = dices // useless ?
        )
    }

    fun executeDices() {
        var smash = 0
        var game = 0
        var smashMeter = 0
        var stock = 0
        state.value.dices.forEach { dice ->
            when (dice) {
                Dice.ONE -> smash += 1
                Dice.TWO -> smash += 2
                Dice.THREE -> smash += 3
                Dice.SMASH -> game += 1
                Dice.SMASH_METER -> smashMeter += 1
                Dice.STOCK -> stock += 1
            }
        }

        val currentPlayer = getCurrentPlayer()
        currentPlayer.play(stock, smashMeter, game)
        state.value.players.forEach { player ->
            if (player.character != currentPlayer.character) {
                player.damaged(smash)
            }
        }
        waitEndTurn()
    }

    fun waitEndTurn() {
        state.value = state.value.copy(
            currentAction = Action.WAIT_END_TURN
        )
    }

    fun endTurn() {
        state.value = state.value.copy(
            currentPlayerIdx = (state.value.currentPlayerIdx + 1) % 4,
            currentAction = Action.THROW_DICES
        )
    }
}