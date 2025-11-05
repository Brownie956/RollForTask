package com.cbmedia.rollforending.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DieDisplay(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Companion.Medium)
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.Companion.size(60.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(contentAlignment = Alignment.Companion.Center) {
                Text(
                    text = value.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Companion.Bold
                )
            }
        }
    }
}