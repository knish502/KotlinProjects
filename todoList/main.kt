import java.io.File

fun main() {
    val filename = "list.txt"
    val contentLines: List<String> = File(filename).readLines()
    var completionMap: MutableMap<String, Boolean> = mutableMapOf()
    var orderedKeyList: MutableList<String> = mutableListOf()
    
    while (true) {
        when (menu()) {
            0 -> viewList()
            1 -> checkOffItem()
            2 -> addItem()
            3 -> removeItem()
            4 -> break
            else -> println("That's not an option, please try again")
        }
    }

    saveData(completionMap, orderedKeyList, filename)
    do {println("Press 'q' to exit:")} while (readLine() != "q")
}

fun menu() : Int {
    val menuText = "\nEnter an option:\n" +
                " 0: View to-do list\n" +
                " 1: Mark item as completed\n" +
                " 2: Add item to to-do list\n" +
                " 3: Remove item from to-do list\n" + 
                " 4: Quit the program\n> "
    println(menuText)
    return readLine()?.toInt() ?: -1
}

fun viewList() {}

fun checkOffItem() {}

fun addItem() {}

fun removeItem() {}

fun saveData(dataMap: MutableMap<String, Boolean>, 
             keyList: MutableList<String>, 
             fileName: String) {}