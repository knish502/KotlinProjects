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
    var hiScore = 0

    init {
        // pass
    }

    fun run() {
        var didPlayerWin = false
        var points = 0
        while(true) {
            didPlayerWin = false
            this.guesses = mutableListOf()
            if (this.score > this.hiScore) {this.hiScore = this.score}
            this.ans = (this.min..this.max).random()
            println("\nCurrent Score: ${this.score} (best: ${this.hiScore})")
            println("Guess a number between ${this.min} and ${this.max}!")
            while (this.guesses.size < this.guessCount) {
                points = this.guessCount - this.guesses.size
                print("You have $points guess")
                if (points != 1) {
                    println("es left.")
                } else {
                    println(" left.")
                }
                print("Your guess:\n> ")
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
                print("You won $points point")
                if (points != 1) {
                    println("s!")
                } else {
                    println("!")
                }
                this.score += points
            } else {
                println("Game Over...")
                println("Your score was $score. Resetting...")
                this.score = 0
            }

            print("Would you like to continue? (Y/N)\n> ")
            if (readLine()?.lowercase() ?: "" == "y") {
                continue
            } else {
                break
            }

        }

        println("Thank you for playing! Your final best was ${this.hiScore}.")
    }


}