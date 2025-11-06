package com.cbmedia.rollforending.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.cbmedia.rollforending.games.FitnessGame
import com.cbmedia.rollforending.models.DiceRoll
import com.cbmedia.rollforending.models.DiceStatus
import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task
import com.cbmedia.rollforending.ui.Dice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        x.value = DiceRoll(name = game.diceXName)
        y.value = DiceRoll(name = game.diceYName)
        z.value = DiceRoll(name = game.diceZName)
    }

    fun rollDiceWithAnimation(
        onUpdate: (Triple<Int, Int, Int>) -> Unit,
        onFinal: (Triple<Int, Int, Int>) -> Unit
    ) {
        var interimRoll = Triple(0, 0, 0)

        viewModelScope.launch {
            // Simulate rolling animation
            repeat(15) { // number of "spin frames"
                interimRoll = Dice.rollDiceWithBias()
                onUpdate(interimRoll)
                delay(80L) // speed of roll updates
            }

            // Final stable roll - Update one after the other
            val finalRoll = Dice.rollDiceWithBias()
            onUpdate(Triple(finalRoll.first, interimRoll.second, interimRoll.third))
            delay(400L)

            onUpdate(Triple(finalRoll.first, finalRoll.second, interimRoll.third))
            delay(400L)

            onUpdate(Triple(finalRoll.first, finalRoll.second, finalRoll.third))
            delay(400L)

            processDiceRolls(rollX = finalRoll.first, rollY = finalRoll.second, rollZ = finalRoll.third)
            onFinal(finalRoll)
        }
    }

    fun generateTask() {
        if (isGameOver.value) return

        val game = selectedGame.value

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
        while (!isGameOver.value) {
            val diceRoll = Dice.rollDiceWithBias()
            processDiceRolls(rollX = diceRoll.first, rollY = diceRoll.second, rollZ = diceRoll.third)
            generateTask()
        }
    }

    fun showFinalDialog() {
        showFinalDialog.value = true
    }

    fun saveDiceRolls(rollX: DiceRoll, rollY: DiceRoll, rollZ: DiceRoll) {
        x.value = rollX
        y.value = rollY
        z.value = rollZ
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

    private fun processDiceRolls(rollX: Int, rollY: Int, rollZ: Int) {
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

        saveDiceRolls(
            rollX = DiceRoll(name = selectedGame.value.diceXName, rollValue = rollX, diceStatus = xStatus),
            rollY = DiceRoll(name = selectedGame.value.diceYName, rollValue = rollY, diceStatus = yStatus),
            rollZ = DiceRoll(name = selectedGame.value.diceZName, rollValue = adjustedZRoll, preProcessedRoll = rollZ, diceStatus = zStatus)
        )
    }
}
