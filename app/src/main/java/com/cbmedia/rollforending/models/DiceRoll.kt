package com.cbmedia.rollforending.models

data class DiceRoll(
    val name: String,
    val rollValue: Int = 0,
    val diceStatus: DiceStatus = DiceStatus.SINGLE,
)

enum class DiceStatus {
    SINGLE,
    DOUBLE,
    TRIPLE
}
