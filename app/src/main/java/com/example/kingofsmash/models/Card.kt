package com.example.kingofsmash.models

import android.os.Parcelable
import com.example.kingofsmash.enums.CardType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(val cost: Int, val description: String, val type: CardType) :
    Parcelable