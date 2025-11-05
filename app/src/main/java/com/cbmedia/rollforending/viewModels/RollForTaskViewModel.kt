package com.cbmedia.rollforending.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.cbmedia.rollforending.games.FitnessGame
import com.cbmedia.rollforending.models.DiceRoll
import com.cbmedia.rollforending.models.DiceStatus
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
        var xStatus: DiceStatus = DiceStatus.SINGLE
        var yStatus: DiceStatus = DiceStatus.SINGLE
        var zStatus: DiceStatus = DiceStatus.SINGLE
        var adjustedZRoll: Int = rollZ

        // Determine if trips, dubs or neither and assign DiceRolls
        if (rollX == rollY && rollY == rollZ) {
            // Must be triple
            xStatus = DiceStatus.TRIPLE
            yStatus = DiceStatus.TRIPLE
            zStatus = DiceStatus.TRIPLE

            score.intValue -= 5
        } else if (rollX == rollY || rollX == rollZ || rollY == rollZ) {
            // Must be double
            adjustedZRoll = max(0, rollZ - 5)

            when (rollX) {
                rollY -> {
                    xStatus = DiceStatus.DOUBLE
                    yStatus = DiceStatus.DOUBLE
                    zStatus = DiceStatus.SINGLE
                }
                rollZ -> {
                    xStatus = DiceStatus.DOUBLE
                    yStatus = DiceStatus.SINGLE
                    zStatus = DiceStatus.DOUBLE
                }
                else -> {
                    xStatus = DiceStatus.SINGLE
                    yStatus = DiceStatus.DOUBLE
                    zStatus = DiceStatus.DOUBLE
                }
            }
        }

        x.value = DiceRoll(name = selectedGame.value.diceXName, rollValue = rollX, diceStatus = xStatus)
        y.value = DiceRoll(name = selectedGame.value.diceYName, rollValue = rollY, diceStatus = yStatus)
        z.value = DiceRoll(name = selectedGame.value.diceZName, rollValue = adjustedZRoll, diceStatus = zStatus)
    }
}
