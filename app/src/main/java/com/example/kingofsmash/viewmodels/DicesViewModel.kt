package com.example.kingofsmash.viewmodels

import androidx.lifecycle.ViewModel
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.models.RollDice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import kotlin.random.Random


data class Dices(
    var dices: List<RollDice> = (1..6).map { RollDice() },
    var roll: Int = 3,
)

class DicesViewModel : ViewModel() {
    private val state = MutableStateFlow(Dices())
    val stateFlow = state.asStateFlow()

    init {
        rollDice()
    }

    fun rollDice() {
        if (state.value.roll <= 0) return
        val random = Random(Date().time)
        state.value = state.value.copy(
            dices = state.value.dices.map { if (it.shouldKeep) it else Dice.getRandom(random).toRollDice() },
            roll = state.value.roll - 1
        )
    }

    fun toggleKeepDice(idx: Int) {
        state.value = state.value.copy(
            dices = state.value.dices.mapIndexed { index, rollDice ->
                if (index == idx) rollDice.copy(shouldKeep = !rollDice.shouldKeep) else rollDice
            }
        )
    }

}