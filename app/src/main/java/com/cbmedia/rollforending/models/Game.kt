package com.cbmedia.rollforending.models

interface Game {
    val title: String
    val categoryAName: String
    val categoryBName: String
    val categoryCName: String

    fun determineCategoryATask(x: Int, y: Int): Task
    fun determineCategoryBTask(index: Int): Task
    fun determineCategoryCTask(index: Int): Task
}