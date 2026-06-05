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

    private val _phase = MutableStateFlow(WordGamePhase.PLAYING)
    val phase: StateFlow<WordGamePhase> = _phase.asStateFlow()

    // _hintUsed must be declared before _grid — buildGrid() resets it during initialization
    private val _hintUsed = MutableStateFlow(false)
    val hintUsed: StateFlow<Boolean> = _hintUsed.asStateFlow()

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    private val _grid = MutableStateFlow(buildGrid(wordList.random()))
    val grid: StateFlow<WordGrid> = _grid.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

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
        // Prevent selecting beyond word length — extra letters would silently break validation
        if (current.selectedIndices.size >= current.word.length) return
        _grid.value = current.copy(
            cells = current.cells.toMutableList().also { it[index] = it[index].copy(isSelected = true) },
            selectedIndices = current.selectedIndices + index
        )
    }

    fun useHint() {
        if (_hintUsed.value) return
        _hintUsed.value = true
        _score.value = maxOf(0, _score.value - 1)
        val firstChar = _grid.value.word[0]
        val cellIndex = _grid.value.cells.indexOfFirst { !it.isSelected && it.char == firstChar }
        if (cellIndex != -1) selectCell(cellIndex)
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
        _phase.value = WordGamePhase.PLAYING
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
            _bestScore.value = maxOf(_bestScore.value, _score.value)
            _phase.value = WordGamePhase.GAME_OVER
        }
    }

    private fun buildGrid(word: String): WordGrid {
        _hintUsed.value = false
        val cells = (word.map { Cell(it) } + List(RANDOM_LETTER_COUNT) { Cell(('A'..'Z').random()) }).shuffled()
        return WordGrid(word = word, cells = cells, selectedIndices = emptyList())
    }
}
