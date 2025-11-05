package com.cbmedia.rollforending.models

data class DiceRoll(
    val name: String,
    val rollValue: Int = 0,
    val isDouble: Boolean = false,
    val isTriple: Boolean = false
)
