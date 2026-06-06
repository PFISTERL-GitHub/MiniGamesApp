package com.pfisterludovicmiehealix.minigames.ui.wordgame

data class WordGrid(
    val word: String,
    val cells: List<Cell>,
    val selectedIndices: List<Int>
) {
    val input: String get() = selectedIndices.map { cells[it].char }.joinToString("")
}
