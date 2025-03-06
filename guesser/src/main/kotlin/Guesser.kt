package org.example
import kotlin.math.log2
import kotlin.math.floor

class Guesser {
    var keepRunning = true
    var guesses = emptyArray<Int>()
    var ans = 0
    var min = 0
    var max = 1000
    var guessCount = floor(log2(this.max.toDouble())).toInt() + 1

    enum class MenuType {
        eTopMenu,
        eParametersMenu
    }

    init {
        // pass
    }

    fun run() {

    }


}