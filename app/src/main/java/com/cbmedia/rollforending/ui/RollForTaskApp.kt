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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbmedia.rollforending.models.DiceRoll
import com.cbmedia.rollforending.models.DiceStatus
import com.cbmedia.rollforending.models.GameRegistry
import com.cbmedia.rollforending.viewModels.RollForTaskViewModel

@Composable
fun RollForTaskApp(
    viewModel: RollForTaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedGame by viewModel.selectedGame
    val currentTask by viewModel.currentTask
    val completedTasks = viewModel.completedTasks
    val score by viewModel.score
    val isGameOver by viewModel.isGameOver
    val showFinalDialog by viewModel.showFinalDialog

    if (showFinalDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("🏁 Game Summary") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Final Score: $score", fontWeight = FontWeight.Bold)
                    Text("Tasks Completed: ${completedTasks.size}\n")
                    completedTasks.forEachIndexed { index, task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "${index + 1}. ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            if (task.category == selectedGame.categoryCName) {
                                Text(text = "[${task.category}] ${task.name}")
                            } else {
                                Column {
                                    if (task.points < 0) {
                                        Text(text = "😈️UNLUCKY! 😈")
                                        Text(text = "[${task.category}] ${task.name} (${task.points})")
                                    } else {
                                        Text(text = "[${task.category}] ${task.name} (+${task.points})")
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.startNewGame(selectedGame) }) { Text("Restart") }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = selectedGame.backgroundColor ?: MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dropdown to choose game
            GameSelector(
                games = GameRegistry.allGames,
                selectedGame = selectedGame,
                onGameSelected = { viewModel.startNewGame(it) }
            )

            // Current score
            Text("Score: $score", fontSize = 22.sp)

            // Dice display
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DieDisplay(viewModel.x.value)
                DieDisplay(viewModel.y.value)
                DieDisplay(viewModel.z.value)
            }

            // Game run buttons
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (isGameOver) {
                    Button(onClick = { viewModel.showFinalDialog() }) {
                        Text("End game")
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.rollDiceWithAnimation(
                                onUpdate = { (x, y, z) ->
                                    viewModel.saveDiceRolls(
                                        rollX = DiceRoll(name = selectedGame.diceXName, rollValue = x, diceStatus = DiceStatus.SINGLE),
                                        rollY = DiceRoll(name = selectedGame.diceYName, rollValue = y, diceStatus = DiceStatus.SINGLE),
                                        rollZ = DiceRoll(name = selectedGame.diceZName, rollValue = z, diceStatus = DiceStatus.SINGLE)
                                    )
                                },
                                onFinal = { viewModel.generateTask() }
                            )
                        }
                    ) {
                        Text("Roll Dice")
                    }
                    Button(onClick = { viewModel.simulateFullGame() }) { Text("Simulate Game") }
                }
            }

            // Current task
            currentTask?.let {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Text(it.category, fontSize = 18.sp)
                Text(it.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Points earned: ${it.points}", fontSize = 18.sp)
            }

            // Completed task list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                itemsIndexed(completedTasks) { index, task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${index + 1}. ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (task.category == selectedGame.categoryCName) {
                            Text(text = "[${task.category}] ${task.name}")
                        } else {
                            Column {
                                if (task.points < 0) {
                                    Text(text = "😈️UNLUCKY! 😈")
                                    Text(text = "[${task.category}] ${task.name} (${task.points})")
                                } else {
                                    Text(text = "[${task.category}] ${task.name} (+${task.points})")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
