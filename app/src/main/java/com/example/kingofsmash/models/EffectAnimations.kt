package com.example.kingofsmash.models

import com.example.kingofsmash.enums.Dice

data class EffectAnimations (
    val stockAnim: List<EffectAnim>,
    val smashMeterAnim: List<EffectAnim>,
    val gameAnim: List<EffectAnim>,
    val smashAnim: List<EffectAnim>,
    val dice: List<Dice>
) {
}

data class EffectAnim (
    val player: Player,
    val variations: List<Int>,
    val variation: Int
) {
}