package com.example.kingofsmash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.enums.PlayerType
import com.example.kingofsmash.models.EffectAnim
import com.example.kingofsmash.models.EffectAnimations
import com.example.kingofsmash.models.KingOfSmash
import com.example.kingofsmash.models.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KingOfSmashViewModel(character: Character) : ViewModel() {
    private val state = MutableStateFlow(
        KingOfSmash(
            Character.values().map { Player(if (character == it) PlayerType.PLAYER else PlayerType.BOT, it) }, 0, null
        )
    )
    val stateFlow = state.asStateFlow()

    fun getPlayers(): List<Player> = state.value.players
    fun getCurrentPlayer(): Player = state.value.players[state.value.currentPlayerIdx]
    fun throwDices(dices: List<Dice>) {
        state.value = state.value.copy(
            currentAction = Action.EXECUTE_DICES, dices = dices
        )
    }

    fun computeDiceAnimations(): EffectAnimations {
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
        val game = (if (ones > 2) ones - 2 else 0) + (if (twos > 2) twos - 1 else 0) + (if (threes > 2) threes else 0)
        val currentPlayer = getCurrentPlayer()
        val playerInDF = state.value.playerInDF

        val stockAnim = mutableListOf<EffectAnim>()
        var stockAnimUpperBound = (currentPlayer.stock + stock).coerceAtMost(currentPlayer.maxStock)
        if (stock > 0 && stockAnimUpperBound > currentPlayer.stock)
            stockAnim.add(EffectAnim(currentPlayer, (currentPlayer.stock + 1..stockAnimUpperBound).toList(), stock))
        val smashMeterAnim = mutableListOf<EffectAnim>()
        if (smashMeter > 0)
            smashMeterAnim.add(EffectAnim(currentPlayer, (currentPlayer.smashMeter + 1..currentPlayer.smashMeter + smashMeter).toList(), smashMeter))
        val gameAnim = mutableListOf<EffectAnim>()
        if (game > 0)
            gameAnim.add(EffectAnim(currentPlayer, (currentPlayer.game + 1..currentPlayer.game + game).toList(), game))

        val smashAnim = mutableListOf<EffectAnim>()
        if (smash > 0 && playerInDF != null) {
            if (currentPlayer == playerInDF) {
                for (player in state.value.players) {
                    if (player != currentPlayer && player.isAlive) {
                        smashAnim.add(EffectAnim(player, (player.stock - 1 downTo  (player.stock - smash).coerceAtLeast(0)).toList(), -smash))
                    }
                }
            } else {
                smashAnim.add(EffectAnim(playerInDF, (playerInDF.stock - 1 downTo  (playerInDF.stock - smash).coerceAtLeast(0)).toList(), -smash))
            }
        }

        return EffectAnimations(stockAnim, smashMeterAnim, gameAnim, smashAnim, state.value.dices)
    }

    fun executeDices(): Boolean {
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

        val game = (if (ones > 2) ones - 2 else 0) + (if (twos > 2) twos - 1 else 0) + (if (threes > 2) threes else 0)

        var isPlayerAttackedAndInDF = false
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
                    state.value.rank = player.damaged(smash, state.value.rank, currentPlayer)
                }
            }
        } else {
            Log.d("ExecuteDices", "DF Player ${state.value.playerInDF?.character} take $smash dmg from ${currentPlayer.character}")
            var res = state.value.playerInDF?.damaged(smash, state.value.rank, currentPlayer)
            if (res != null)
                state.value.rank = res
            if (state.value.playerInDF != null) {
                if (!state.value.playerInDF!!.isAlive) {
                    Log.d("ExecuteDices", "player ${state.value.playerInDF?.character} is dead")
                    state.value = state.value.copy(playerInDF = null)
                } else if (state.value.playerInDF!!.type == PlayerType.PLAYER) {
                    Log.d("ExecuteDices", "ask player in DF ${state.value.playerInDF?.character}")
                    isPlayerAttackedAndInDF = true
                }
            }
        }
        return isPlayerAttackedAndInDF
    }

    fun leaveDFAndCheck() {
        Log.d("LeaveDF", "player leaving DF")
        state.value = state.value.copy(playerInDF = null, currentAction = Action.CHECK_DF)
    }

    fun setDFAttacked() {
        state.value = state.value.copy(currentAction = Action.DF_ATTACKED)
    }

    fun setCheckDF() {
        state.value = state.value.copy(currentAction = Action.CHECK_DF)
    }

    fun checkDF(): Boolean {
        Log.d("CheckDF", "Checking DF")
        val currentPlayer = getCurrentPlayer()
        var isPlayerInDF = false
        if (state.value.playerInDF == null) {
            Log.d("CheckDF", "player ${currentPlayer.character} is now in DF")
            state.value = state.value.copy(playerInDF = currentPlayer)
            isPlayerInDF = true
        }
        return isPlayerInDF
    }

    fun setCheckGameOver() {
        state.value = state.value.copy(currentAction = Action.CHECK_GAME_OVER)
    }

    fun waitEndTurn() {
        state.value = state.value.copy(
            currentAction = Action.WAIT_END_TURN
        )
    }

    fun endTurn() {
        state.value = state.value.copy(
            currentPlayerIdx = (state.value.currentPlayerIdx + 1) % 4, currentAction = Action.THROW_DICES
        )
    }

    fun getWinner(): Player? {
        var winner: Player? = null
        var alivePlayer = 0
        for (player in state.value.players) {
            if (player.game >= NB_GAME_TO_WIN) {
                return player
            }
            if (player.isAlive) {
                alivePlayer++
                winner = player
            }
        }
        return if (alivePlayer == 1) winner else null
    }

    companion object {
        private const val NB_GAME_TO_WIN = 20
    }
}