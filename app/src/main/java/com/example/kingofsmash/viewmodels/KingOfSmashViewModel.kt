package com.example.kingofsmash.viewmodels

import android.util.Log
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
            Character.values().map { Player(if (character == it) PlayerType.PLAYER else PlayerType.BOT, it) },
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
        var ones = 0
        var twos = 0
        var threes = 0
        var smash = 0
        var smashMeter = 0
        var stock = 0
        state.value.dices.forEach { dice ->
            when (dice) {
                Dice.ONE -> ones++
                Dice.TWO -> twos++
                Dice.THREE -> threes++
                Dice.SMASH -> smash++
                Dice.SMASH_METER -> smashMeter++
                Dice.STOCK -> stock++
            }
        }

        val game = (if (ones > 1) ones - 1 else 0) + (if (twos > 1) twos else 0) + (if (threes > 1) threes + 1 else 0)

        val currentPlayer = getCurrentPlayer()
        Log.d(
            "ExecuteDices",
            "player ${currentPlayer.character} plays smash: $smash, game: $game, smashMeter: $smashMeter, stock: $stock"
        )
        currentPlayer.play(stock, smashMeter, game)
        if (state.value.playerInDF == currentPlayer) {
            state.value.players.forEach { player ->
                if (player != currentPlayer) {
                    Log.d("ExecuteDices", "player ${player.character} take $smash dmg from ${currentPlayer.character}")
                    player.damaged(smash)
                }
            }
        } else {
            state.value.playerInDF?.damaged(smash)
            if (state.value.playerInDF?.isAlive == false) {
                Log.d("ExecuteDices", "player ${state.value.playerInDF?.character} is dead")
                state.value = state.value.copy(playerInDF = null)
            }
        }

        if (state.value.playerInDF == null) {
            Log.d("ExecuteDices", "player ${currentPlayer.character} is now in DF")
            state.value = state.value.copy(playerInDF = currentPlayer)
        }

        // TODO: check if game is over

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