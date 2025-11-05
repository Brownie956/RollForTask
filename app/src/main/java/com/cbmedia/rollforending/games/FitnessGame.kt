package com.cbmedia.rollforending.games

import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task

object FitnessGame: Game {
    override val title: String = "💪Fitness Game"
    override val categoryAName: String = "Hopping"
    override val categoryBName: String = "Running"
    override val categoryCName: String = "Finishing"

    override fun determineCategoryATask(x: Int, y: Int): Task {
        val hops = 150 + (x * 20)
        val rate = 100 + (y * 10)
        return Task("Do $hops hops at $rate hops per minute", x, categoryAName)
    }

    override fun determineCategoryBTask(index: Int): Task {
        val task = listOf(
            Pair("Easy points", 1),
            Pair("Run twice with 10s rest in between", 3),
            Pair("Run three times with 10s rest in between", 4),
            Pair("Run once for 20s", 4),
            Pair("Run three times for 15s with 15s rest in between", 8),
            Pair("Run three times for 30s with 20s rest in between", 10),
            Pair("Run twice for 1 minute with 20s rest in between", 12),
            Pair("Run twice for 2 minutes with 30s rest in between", 15),
            Pair("Run once for 5 minutes", 20),
            Pair("Unlucky", -5)
        )[index]

        return Task(name = task.first, points = task.second, category = categoryBName)
    }

    override fun determineCategoryCTask(index: Int): Task {
        val task = when (index) {
            in 0..2 -> "Do nothing and finish the game"
            in 3..5 -> "Run once more until you stop running and then finish the game"
            in 6..8 -> "Run ten times with ankle weights and then finish the game"
            else -> "Ten press ups and finish the game"
        }

        return Task(name = task, points = 0, category = categoryCName)
    }
}
