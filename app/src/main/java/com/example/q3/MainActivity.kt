package com.example.q3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.example.q3.ui.theme.Q3Theme

class MainActivity : ComponentActivity() {

    private val vm: TemperatureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Q3Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val state by vm.state.collectAsState()
                    TemperatureDashboard(
                        state = state,
                        onToggle = vm::toggle
                    )
                }
            }
        }
    }
}
