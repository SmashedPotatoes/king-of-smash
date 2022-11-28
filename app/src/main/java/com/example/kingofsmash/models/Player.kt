package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.PlayerType
import kotlin.math.min

data class Player(
    val type: PlayerType,
    val character: Character,
    var stock: Int = 10,
    var smashMeter: Int = 0,
    var game: Int = 0,
    var isAlive: Boolean = true,
) {
    fun heal(stock: Int) {
        this.stock = min(this.stock + stock, MAX_STOCK)
    }

    fun energize(smashMeter: Int) {
        this.smashMeter += smashMeter
    }

    fun win(game: Int) {
        this.game += game
    }

    fun damaged(smash: Int) {
        this.stock = (this.stock - smash).coerceAtLeast(0)
        if (this.stock <= 0) {
            this.isAlive = false
        }
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