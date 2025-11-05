package com.cbmedia.rollforending.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.cbmedia.rollforending.games.FitnessGame
import com.cbmedia.rollforending.models.DiceRoll
import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task
import com.cbmedia.rollforending.ui.Dice
import kotlin.math.max

class RollForTaskViewModel: ViewModel() {

    // Dice
    val x = mutableStateOf(DiceRoll(name = "X"))
    val y = mutableStateOf(DiceRoll(name = "Y"))
    val z = mutableStateOf(DiceRoll(name = "Z"))

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
        saveDiceRolls(rollX, rollY, rollZ)

        // Determine task
        val category = determineCategory(score.intValue, z.value.rollValue)
        val task = when(category) {
            game.categoryAName -> game.determineCategoryATask(x.value.rollValue, y.value.rollValue)
            game.categoryBName -> game.determineCategoryBTask(y.value.rollValue)
            else -> game.determineCategoryCTask(z.value.rollValue)
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

    private fun saveDiceRolls(rollX: Int, rollY: Int, rollZ: Int) {
        // Determine if trips, dubs or neither and assign DiceRolls
        if (rollX == rollY && rollY == rollZ) {
            // Must be triple
            x.value = DiceRoll(name = "X", rollValue = rollX, isTriple = true)
            y.value = DiceRoll(name = "Y", rollValue = rollY, isTriple = true)
            z.value = DiceRoll(name = "Z", rollValue = rollZ, isTriple = true)
            score.intValue -= 5
        } else if (rollX == rollY || rollX == rollZ || rollY == rollZ) {
            // Must be double
            when (rollX) {
                rollY -> {
                    x.value = DiceRoll(name = "X", rollValue = rollX, isDouble = true)
                    y.value = DiceRoll(name = "Y", rollValue = rollY, isDouble = true)
                    z.value = DiceRoll(name = "Z", rollValue = max(0, rollZ - 5))
                }
                rollZ -> {
                    x.value = DiceRoll(name = "X", rollValue = rollX, isDouble = true)
                    y.value = DiceRoll(name = "Y", rollValue = rollY)
                    z.value = DiceRoll(name = "Z", rollValue = max(0, rollZ - 5), isDouble = true)
                }
                else -> {
                    x.value = DiceRoll(name = "X", rollValue = rollX)
                    y.value = DiceRoll(name = "Y", rollValue = rollY, isDouble = true)
                    z.value = DiceRoll(name = "Z", rollValue = max(0, rollZ - 5), isDouble = true)
                }
            }
        } else {
            // Neither triple or double
            x.value = DiceRoll(name = "X", rollValue = rollX)
            y.value = DiceRoll(name = "Y", rollValue = rollY)
            z.value = DiceRoll(name = "Z", rollValue = rollZ)
        }
    }
}
