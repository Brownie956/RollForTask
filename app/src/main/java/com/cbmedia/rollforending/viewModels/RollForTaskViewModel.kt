package com.cbmedia.rollforending.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.cbmedia.rollforending.games.FitnessGame
import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task
import com.cbmedia.rollforending.ui.Dice
import kotlin.math.max

class RollForTaskViewModel: ViewModel() {

    // Dice
    val x = mutableIntStateOf(0)
    val y = mutableIntStateOf(0)
    val z = mutableIntStateOf(0)

    val completedTasks = mutableStateListOf<Task>()
    val currentTask = mutableStateOf<Task?>(null)
    val isGameOver = mutableStateOf(false)
    val score = mutableIntStateOf(0)
    // State
    val selectedGame = mutableStateOf<Game>(FitnessGame)
    val showFinalDialog = mutableStateOf(false)

    // Logic
    fun startNewGame(game: Game) {
        selectedGame.value = game
        completedTasks.clear()
        score.intValue = 0
        isGameOver.value = false
        currentTask.value = null
        showFinalDialog.value = false
    }

    fun rollDiceAndGenerateTask() {
        if (isGameOver.value) return

        val game = selectedGame.value

        val (rollX, rollY, rollZ) = Dice.rollDiceWithBias()
        x.intValue = rollX
        y.intValue = rollY
        z.intValue = rollZ

        // Account for dubs and trips
        if (x == y || y == z || x == z) z.intValue = max(0, z.intValue - 5)
        else if (x == y && y == z) score.intValue -= 5

        // Determine task
        val category = determineCategory(score.intValue, z.intValue)
        val task = when(category) {
            game.categoryAName -> game.determineCategoryATask(x.intValue, y.intValue)
            game.categoryBName -> game.determineCategoryBTask(y.intValue)
            else -> game.determineCategoryCTask(z.intValue)
        }

        currentTask.value = task
        completedTasks.add(task)
        score.intValue += task.points

        // End game if it's the final category
        if (task.category == game.categoryCName) isGameOver.value = true
    }

    fun simulateFullGame() {
        startNewGame(selectedGame.value)
        while (!isGameOver.value) rollDiceAndGenerateTask()
    }

    fun showFinalDialog() {
        showFinalDialog.value = true
    }

    private fun determineCategory(currentScore: Int, z: Int): String {
        val adjustedScore = currentScore + z
        if (adjustedScore < 20) return selectedGame.value.categoryAName
        else if (adjustedScore < 60) return selectedGame.value.categoryBName
        else if (adjustedScore < 70) return selectedGame.value.categoryCName
        else {
            score.intValue = 30
            return selectedGame.value.categoryBName
        }
    }
}