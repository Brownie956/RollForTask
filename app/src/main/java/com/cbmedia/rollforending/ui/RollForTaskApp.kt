package com.cbmedia.rollforending.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbmedia.rollforending.models.Game
import com.cbmedia.rollforending.models.Task
import com.cbmedia.rollforending.ui.Dice.rollDiceWithBias
import kotlin.math.max

@Composable
fun RollForTaskApp(
    game: Game,
    modifier: Modifier = Modifier
) {
    var x by remember { mutableStateOf(0) }
    var y by remember { mutableStateOf(0) }
    var z by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var currentTask by remember { mutableStateOf<Task?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    val completedTasks = remember { mutableStateListOf<Task>() }
    var showDialog by remember { mutableStateOf(false) }

    fun determineCategory(currentScore: Int, z: Int): String {
        val adjustedScore = currentScore + z
        if (adjustedScore < 20) return game.categoryAName
        else if (adjustedScore < 60) return game.categoryBName
        else if (adjustedScore < 70) return game.categoryCName
        else {
            score = 30
            return game.categoryBName
        }
    }

    fun determineTask(category: String, x: Int, y: Int, z: Int): Task {
        return when (category) {
            game.categoryAName -> game.determineCategoryATask(x, y)
            game.categoryBName -> game.determineCategoryBTask(y)
            game.categoryCName -> game.determineCategoryCTask(z)
            else -> Task("Unknown task", 0, category)
        }
    }

    fun rollOnce(): Task {
        val (newX, newY, newZ) = rollDiceWithBias()
        x = newX
        y = newY
        z = newZ

        if (x == y && y == z) score -= 5
        else if (x == y || y == z || x == z) z = max(0, z - 5)

        val category = determineCategory(score, z)
        val task = determineTask(category, x, y, z)
        score += task.points
        completedTasks.add(task)
        currentTask = task

        if (category == game.categoryCName) gameOver = true
        return task
    }

    fun rollDice() {
        if (gameOver) return
        rollOnce()
        if (gameOver) showDialog = true
    }

    fun simulateGame() {
        while (!gameOver) rollOnce()
        showDialog = true
    }

    fun resetGame() {
        x = 0; y = 0; z = 0
        score = 0
        gameOver = false
        completedTasks.clear()
        currentTask = null
        showDialog = false
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("🏁 Game Summary") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Final Score: $score", fontWeight = FontWeight.Bold)
                    Text("Tasks Completed: ${completedTasks.size}\n")
                    completedTasks.forEachIndexed { index, t ->
                        Text("${index + 1}. [${t.category}] ${t.name} (+${t.points})")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { resetGame() }) { Text("Restart") }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(game.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Score: $score", fontSize = 22.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DieDisplay("X", x)
                DieDisplay("Y", y)
                DieDisplay("Z", z)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { rollDice() }) { Text("Roll Dice") }
                Button(onClick = { simulateGame() }) { Text("Simulate Game") }
            }
            currentTask?.let {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(it.category, fontSize = 18.sp)
                Text(it.name, fontSize = 20.sp)
                Text("Points earned: ${it.points}", fontSize = 18.sp)
            }

            LazyColumn {
                itemsIndexed(completedTasks) { index, task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${index + 1}. ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (task.category == game.categoryCName) {
                            Text(text = "[${task.category}] ${task.name}")
                        } else {
                            Text(text = "[${task.category}] ${task.name} (+${task.points})")
                        }
                    }
                }
            }
        }
    }
}
