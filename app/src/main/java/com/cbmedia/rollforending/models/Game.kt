package com.cbmedia.rollforending.models

interface Game {
    val diceXName: String
        get() = "X"
    val diceYName: String
        get() = "Y"
    val diceZName: String
        get() = "Z"

    val title: String
    val categoryAName: String
    val categoryBName: String
    val categoryCName: String

    fun determineCategoryATask(x: Int, y: Int): Task
    fun determineCategoryBTask(index: Int): Task
    fun determineCategoryCTask(index: Int): Task
}