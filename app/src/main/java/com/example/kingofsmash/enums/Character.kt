package com.example.kingofsmash.enums

import com.example.kingofsmash.R

enum class Character(val character: String, val icon: Int, val df: Int, val gameOverBackground: Int) {
    LUCAS("Lucas", R.drawable.icon_lucas, R.drawable.icon_lucas, R.drawable.card_image_lucas),
    ROY("Roy", R.drawable.icon_roy, R.drawable.icon_roy, R.drawable.card_image_roy),
    KINGDDD("King DDD", R.drawable.icon_kingddd, R.drawable.icon_kingddd, R.drawable.card_image_kingddd),
    CORRIN("Corrin", R.drawable.icon_corrin, R.drawable.icon_corrin, R.drawable.card_image_corrin),
}