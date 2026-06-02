package com.pfisterludovicmiehealix.minigames.ui.wordgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordGameViewModel : ViewModel() {

    enum class Phase { PLAYING, GAME_OVER }

    data class Cell(
        val char: Char,
        val isSelected: Boolean = false
    )

    data class WordGrid(
        val word: String,
        val cells: List<Cell>,
        val selectedIndices: List<Int>
    ) {
        val input: String get() = selectedIndices.map { cells[it].char }.joinToString("")
    }

    companion object {
        const val TIMER_DURATION_SECONDS = 60
        const val RANDOM_LETTER_COUNT = 3
        const val TIMER_TICK_MS = 1000L
    }

    private val wordList = listOf(
        "SOLEIL", "MAISON", "JARDIN", "CHEMIN", "BOUTON",
        "MIROIR", "PLANTE", "CARTON", "FUSEAU", "CITRON",
        "VIOLON", "RAPIDE", "BLOQUE", "MOUTON", "GATEAU"
    )

    // _phase is mutable internally; phase is the read-only public view
    private val _phase = MutableStateFlow(Phase.PLAYING)
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    // _grid is mutable internally; grid is the read-only public view
    private val _grid = MutableStateFlow(buildGrid(wordList.random()))
    val grid: StateFlow<WordGrid> = _grid.asStateFlow()

    // TP-3: move score persistence to ScoreRepository
    // _score is mutable internally; score is the read-only public view
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // _timer is mutable internally; timer is the read-only public view
    private val _timer = MutableStateFlow(TIMER_DURATION_SECONDS)
    val timer: StateFlow<Int> = _timer.asStateFlow()

    private var timerJob: Job? = null

    fun startGame() {
        _timer.value = TIMER_DURATION_SECONDS
        _grid.value = buildGrid(wordList.random())
        launchTimer()
    }

    fun selectCell(index: Int) {
        val current = _grid.value
        if (current.cells[index].isSelected) return
        _grid.value = current.copy(
            cells = current.cells.toMutableList().also { it[index] = it[index].copy(isSelected = true) },
            selectedIndices = current.selectedIndices + index
        )
    }

    fun eraseLast() {
        val current = _grid.value
        if (current.selectedIndices.isEmpty()) return
        val lastIndex = current.selectedIndices.last()
        _grid.value = current.copy(
            cells = current.cells.toMutableList().also { it[lastIndex] = it[lastIndex].copy(isSelected = false) },
            selectedIndices = current.selectedIndices.dropLast(1)
        )
    }

    fun validate() {
        if (_grid.value.input == _grid.value.word) _score.value++
        _grid.value = buildGrid(wordList.random())
    }

    fun pass() {
        _grid.value = buildGrid(wordList.random())
    }

    fun reset() {
        timerJob?.cancel()
        _score.value = 0
        _timer.value = TIMER_DURATION_SECONDS
        _phase.value = Phase.PLAYING
        _grid.value = buildGrid(wordList.random())
        launchTimer()
    }

    private fun launchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timer.value > 0) {
                delay(TIMER_TICK_MS)
                _timer.value--
            }
            _phase.value = Phase.GAME_OVER
        }
    }

    private fun buildGrid(word: String): WordGrid {
        val cells = (word.map { Cell(it) } + List(RANDOM_LETTER_COUNT) { Cell(('A'..'Z').random()) }).shuffled()
        return WordGrid(word = word, cells = cells, selectedIndices = emptyList())
    }
}
