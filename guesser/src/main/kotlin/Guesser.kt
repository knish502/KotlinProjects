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
        var menuOption = 0
        var parameterMenuOption = 0
        var status = 0
        while (this.keepRunning) {
            menuOption = this.menu(MenuType.eTopMenu)
            when (menuOption) {
                0 -> break
                2 -> parameterMenuOption = this.menu(MenuType.eParametersMenu)
            }
            if (! this.turns()) { break }
        }
    }

    fun menu(menuType: MenuType) : Int {
        val topMenuText = "Choose an option:\n" +
                "\t0 - Exit program\n" +
                "\t1 - New Game\n" +
                "\t2 - Change game parameters\n" +
                "> "

        val adjustGameMenu = "Choose an option:\n" +
                "\t0 - Return to top menu\n" +
                "\t1 - Change minimum guess\n" +
                "\t2 - Change maximum guess\n" +
                "\t3 - Change number of guesses\n" +
                "> "

        when (menuType) {
            MenuType.eTopMenu -> print(topMenuText)
            MenuType.eParametersMenu -> print(adjustGameMenu)
        }

        val inputValue = readLine()?.toInt() ?: 0
        return inputValue
    }

    fun turns() : Boolean {
        println("taking a turn!")
        return false
    }


}