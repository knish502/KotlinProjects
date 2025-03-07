package org.example
import kotlin.math.log2
import kotlin.math.floor

class Guesser {
    var keepRunning = true
    var guesses: MutableList<Int> = mutableListOf()
    var ans = 0
    var min = 0
    var max = 1000
    var guessCount = floor(log2(this.max.toDouble())).toInt() + 1

    init {
        // pass
    }

    fun run() {
        this.ans = (this.min..this.max).random()
        println("Guess a number between ${this.min} and ${this.max}!")
        while (this.guesses.size < this.guessCount) {
            println("You have ${this.guessCount - this.guesses.size} guesses left.")
            print("Write something:\n> ")
            var guess = readLine()?.toInt() ?: 0

            if (guess > this.ans) {
                println("Your guess is too high!\n")
            }
            else if (guess < this.ans) {
                println("Your guess is too low!\n")
            }
            else if (guess == this.ans) {
                println("That's correct!")
                break
            }
            else {
                // something is wrong if you got here
            }

            this.guesses.add(guess)
        }

        println("The answer was: ${this.ans}")
    }


}