package com.cbmedia.rollforending.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbmedia.rollforending.models.DiceRoll
import com.cbmedia.rollforending.models.DiceStatus
import kotlin.random.Random

object Dice {
    fun biasedRoll(): Int {
        val a = Random.Default.nextInt(0, 10)
        val b = Random.Default.nextInt(0, 10)
        return maxOf(a, b)
    }

    fun nonBiasedRoll(): Int {
        return Random.Default.nextInt(0, 10)
    }


    fun rollDiceWithBias(): Triple<Int, Int, Int> {
        val base = biasedRoll()
        val x = base
        val y = if (Random.Default.nextFloat() < 0.4f) base else biasedRoll()
        val z = if (Random.Default.nextFloat() < 0.25f) base else biasedRoll()
        return Triple(x, y, z)
    }
}

@Composable
fun DieDisplay(
    diceRoll: DiceRoll,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (diceRoll.diceStatus) {
        DiceStatus.SINGLE -> MaterialTheme.colorScheme.surface
        DiceStatus.DOUBLE -> Color(0xFFFCE576)
        DiceStatus.TRIPLE -> Color(0xFFFF0012)
    }

    val scale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
    )

    Column(
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(diceRoll.name, fontSize = 18.sp, fontWeight = FontWeight.Companion.Medium)
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale),
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            border = BorderStroke(3.dp, Color.Black)
        ) {
            Box(
                contentAlignment = Alignment.Companion.Center,
            ) {
                if (diceRoll.rollValue == diceRoll.preProcessedRoll) {
                    Text(
                        text = diceRoll.rollValue.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Companion.Bold
                    )
                } else {
                    Row {
                        Text(
                            text = diceRoll.preProcessedRoll.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Companion.Bold,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = " -> ${diceRoll.rollValue}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }
                }
            }
        }
    }
}
