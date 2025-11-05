package com.cbmedia.rollforending

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.cbmedia.rollforending.ui.RollForTaskApp
import com.cbmedia.rollforending.viewModels.RollForTaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold { innerPadding ->
                RollForTaskApp(
                    viewModel = RollForTaskViewModel(),
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
