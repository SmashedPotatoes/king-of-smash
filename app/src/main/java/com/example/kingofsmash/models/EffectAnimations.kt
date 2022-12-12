package com.example.kingofsmash.models

data class EffectAnimations (
    val stockAnim: List<EffectAnim>,
    val smashMeterAnim: List<EffectAnim>,
    val gameAnim: List<EffectAnim>,
    val smashAnim: List<EffectAnim>,
) {
}

data class EffectAnim (
    val player: Player,
    val variations: List<Int>,
    val variation: Int
) {
}