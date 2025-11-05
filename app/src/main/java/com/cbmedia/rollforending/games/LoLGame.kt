package com.cbmedia.rollforending.games

import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task

object LoLGame: Game {
    override val title: String = "💪League of Legends decider"
    override val categoryAName: String = "How much money to save"
    override val categoryBName: String = "CS score"
    override val categoryCName: String = "Win condition"

    override fun determineCategoryATask(x: Int, y: Int): Task {
        return Task("Save ${x + y} gold above current gold level", 5, categoryAName)
    }

    override fun determineCategoryBTask(index: Int): Task {
        return Task(name = "Get a CS score of ${index * 7} more than what you have now", points = index, category = categoryBName)
    }

    override fun determineCategoryCTask(index: Int): Task {
        val task = when (index) {
            in 0..2 -> "Finish with a positive K/D"
            in 3..5 -> "Win the game"
            in 6..8 -> "Don't finish with the lowest total damage score"
            else -> "Have the best warding score"
        }

        return Task(name = task, points = index, category = categoryCName)
    }
}
