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

    init {
        // pass
    }

    fun run() {
        var menuOption = 0
        var status = 0
        while (this.keepRunning) {
            // get menu input
                // check if need to break
            // make changes
            if (! this.turns()) { break }
        }
    }

    fun turns() : Boolean {
        println("taking a turn!")
        return false
    }
}