package com.example.q3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlin.random.Random

// One reading
data class TempReading(val valueF: Float, val timestampMillis: Long = System.currentTimeMillis())

// UI state for the dashboard
data class TempState(
    val readings: List<TempReading> = emptyList(), // newest first
    val running: Boolean = true,                   // auto-generate on/off
    val current: Float? = null,
    val avg: Float? = null,
    val min: Float? = null,
    val max: Float? = null
)

class TemperatureViewModel : ViewModel() {

    private val _state = MutableStateFlow(TempState())
    val state: StateFlow<TempState> = _state

    private var genJob: Job? = null

    init {
        // Start generation by default
        start()
    }

    fun start() {
        if (_state.value.running) return
        _state.value = _state.value.copy(running = true)
        startJob()
    }

    fun pause() {
        _state.value = _state.value.copy(running = false)
        genJob?.cancel()
        genJob = null
    }

    fun toggle() {
        if (_state.value.running) pause() else start()
    }

    private fun startJob() {
        genJob?.cancel()
        genJob = viewModelScope.launch {
            while (true) {
                delay(2000L)
                pushRandomReading()
            }
        }
    }

    // Simulate one reading between 65°F and 85°F
    private fun pushRandomReading() {
        val v = 65f + Random.nextFloat() * (85f - 65f)
        pushReading(TempReading(valueF = (round(v * 10) / 10f)))
    }

    // Keep only last 20, compute stats
    private fun pushReading(r: TempReading) {
        val newList = (listOf(r) + _state.value.readings).take(20)
        val values = newList.map { it.valueF }
        val s = _state.value.copy(
            readings = newList,
            current = values.firstOrNull(),
            avg = if (values.isNotEmpty()) values.average().toFloat() else null,
            min = values.minOrNull(),
            max = values.maxOrNull()
        )
        _state.value = s
    }

    override fun onCleared() {
        genJob?.cancel()
        super.onCleared()
    }
}
