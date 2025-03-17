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
    var score = 0

    init {
        // pass
    }

    fun run() {
        var didPlayerWin = false
        var points = 0
        while(true) {
            didPlayerWin = false
            this.guesses = mutableListOf()
            this.ans = (this.min..this.max).random()
            println("\nCurrent Score: ${this.score}")
            println("Guess a number between ${this.min} and ${this.max}!")
            while (this.guesses.size < this.guessCount) {
                points = this.guessCount - this.guesses.size
                println("You have $points guesses left.")
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
                    didPlayerWin = true
                    break
                }
                else {
                    // something is wrong if you got here
                }

                this.guesses.add(guess)
            }

            println("The answer was: ${this.ans}")
            if (didPlayerWin) {
                println("You won $points points!")
                this.score += points
            } else {
                println("You lost a point...")
                this.score -= 1
            }

            print("Would you like to continue? (Y/N)\n> ")
            if (readLine()?.lowercase() ?: "" == "y") {
                continue
            } else {
                break
            }

        }

        println("Thank you for playing! Your final score was ${this.score}.")
    }


}