package com.example.q3

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter



// ---- Small helpers ----
@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(millis: Long): String {
    val fmt = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return fmt.format(Instant.ofEpochMilli(millis))
}

// Simple, non-experimental top bar
@Composable
private fun SimpleTopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)   // works with NO special import
            )
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
    }
}


// ---- Dashboard UI ----
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TemperatureDashboard(
    state: TempState,
    onToggle: () -> Unit
) {
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "Temperature Dashboard",
                actions = {
                    TextButton(onClick = onToggle) {
                        Text(if (state.running) "Pause" else "Resume")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Stats row
            StatsRow(
                current = state.current,
                avg = state.avg,
                min = state.min,
                max = state.max
            )

            // Line chart (Canvas) from last 20
            TemperatureChart(readings = state.readings)

            // List of readings
            Text("Recent Readings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.readings) { r ->
                    ReadingRow(r)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun StatsRow(current: Float?, avg: Float?, min: Float?, max: Float?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Current", current)
        StatCard("Avg", avg)
        StatCard("Min", min)
        StatCard("Max", max)
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Float?,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value?.let { String.format("%.1f°F", it) } ?: "--",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun TemperatureChart(readings: List<TempReading>) {
    val values = readings.map { it.valueF }.reversed() // oldest -> newest for x spacing
    val min = values.minOrNull() ?: 0f
    val max = values.maxOrNull() ?: 1f
    val range = (max - min).let { if (it < 1f) 1f else it } // avoid div-by-zero

    // FIX: read colors in a composable context, *outside* Canvas draw lambda
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Surface(tonalElevation = 3.dp) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(8.dp)
        ) {
            if (values.size < 2) return@Canvas

            val stepX = size.width / (values.size - 1).coerceAtLeast(1)
            val path = Path()
            values.forEachIndexed { i, v ->
                val x = stepX * i
                val norm = (v - min) / range
                val y = size.height - (norm * size.height)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = primary,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
            // min baseline
            drawLine(
                color = secondary,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1f
            )
            // max baseline
            drawLine(
                color = secondary,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1f
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReadingRow(r: TempReading) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(formatTime(r.timestampMillis), style = MaterialTheme.typography.bodyMedium)
        Text(String.format("%.1f°F", r.valueF), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
