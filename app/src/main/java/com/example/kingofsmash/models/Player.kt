package com.example.kingofsmash.models

import android.os.Parcelable
import android.util.Log
import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.PlayerType
import kotlinx.parcelize.Parcelize
import kotlin.math.min

@Parcelize
data class Player(
    val type: PlayerType,
    val character: Character,
    var stock: Int = 10,
    var smashMeter: Int = 0,
    var game: Int = 0,
    var isAlive: Boolean = true,
    var rank: Int = 0,
    var kills: MutableList<Character> = mutableListOf(),
    var maxStock: Int = 10
) : Parcelable {
    private fun heal(stock: Int) {
        this.stock = min(this.stock + stock, MAX_STOCK)
    }

    private fun energize(smashMeter: Int) {
        this.smashMeter += smashMeter
    }

    private fun win(game: Int) {
        this.game += game
    }

    private fun addKill(character: Character) {
        this.kills.add(character)
    }

    fun damaged(smash: Int, nextRank: Int, attacker: Player): Int {
        if (!isAlive) return nextRank
        this.stock = (this.stock - smash).coerceAtLeast(0)
        if (this.stock <= 0) {
            this.isAlive = false
            this.rank = nextRank
            Log.d("Kill", "player ${this.character} killed by ${attacker.character}")
            attacker.addKill(this.character)
            return nextRank - 1
        }
        return nextRank
    }

    fun play(stock: Int, smashMeter: Int, game: Int) {
        heal(stock)
        energize(smashMeter)
        win(game)
    }

    companion object {
        const val MAX_STOCK = 10
    }
}