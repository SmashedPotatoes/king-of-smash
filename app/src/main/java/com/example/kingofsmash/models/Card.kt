package com.example.kingofsmash.models

data class Card(val card: String, val description: String, val onUse: (KingOfSmash) -> Unit)