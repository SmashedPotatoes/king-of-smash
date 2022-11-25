package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.PlayerType

data class Player(
    val type: PlayerType,
    val character: Character,
    var stock: Int = 10,
    var smashMeter: Int = 0,
    var game: Int = 0,
    var isAlive: Boolean = true,
) {
    fun heal(stock: Int) {
        this.stock += stock
    }

    fun energize(smashMeter: Int) {
        this.smashMeter += smashMeter
    }

    fun win(game: Int) {
        this.game += game
    }

    fun damaged(smash: Int) {
        this.stock -= smash
        if (this.stock <= 0) {
            this.isAlive = false
        }
    }

    fun play(stock: Int, smashMeter: Int, game: Int) {
        heal(stock)
        energize(smashMeter)
        win(game)
    }
}