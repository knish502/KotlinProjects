import java.io.File

fun main() {
    val filename = "list.txt"
    var dataStore = DataStruct(filename)

    while (true) {
        when (menu()) {
            0 -> dataStore.viewList()
            1 -> dataStore.checkOffItem()
            2 -> dataStore.addItem()
            3 -> dataStore.removeItem()
            4 -> break
            else -> println("That's not an option, please try again")
        }
    }

    dataStore.saveData()
    do {println("Press 'q' to confirm:")} while (readlnOrNull() != "q")
}

fun menu() : Int {
    val menuText = "\nEnter an option:\n" +
            " 0: View to-do list\n" +
            " 1: Mark item as completed/incomplete\n" +
            " 2: Add item to to-do list\n" +
            " 3: Remove item from to-do list\n" +
            " 4: Quit the program\n> "
    print(menuText)
    return readLine()?.toInt() ?: -1
}



class DataStruct (private val fileName: String) {

    private var completionMap: MutableMap<String, Boolean> = mutableMapOf<String, Boolean>()
    private var keyList: MutableList<String> = mutableListOf()

    init {
        this.loadData()
        this.keyList = this.completionMap.keys.toMutableList()
    }

    fun loadData() {
        val dataFileLines : List<String> = File(fileName).readLines()
        this.completionMap = mutableMapOf<String, Boolean>()
        var key = ""
        var value = false
        for (line in dataFileLines) {
            value = false
            if (line.take(1) == "*") {
                key = line.substring(1)
                value = true
            }
            else {
                key = line
            }
            this.completionMap[key] = value
        }
    }

    fun viewList() {
        println("\nDisplaying your to-do list:\n")
        for (i in 0..<this.keyList.size){
            print("$i.")
            if (this.completionMap[this.keyList[i]] == true){
                print(" [complete]")
            }
            print(" ${this.keyList[i]}\n")
        }
    }

    fun checkOffItem() {
        print("\nEnter the index of the item you wish to check off or uncheck:\n> ")
        val idx = readlnOrNull()?.toInt() ?: -100
        this.completionMap[this.keyList[idx]] = !this.completionMap[this.keyList[idx]]!!
        println("You have checked '${this.keyList[idx]}'")
    }

    fun addItem() {
        print("\nEnter your task:\n> ")
        val taskName = readlnOrNull() ?: ""
        this.completionMap[taskName] = false
        this.keyList = this.completionMap.keys.toMutableList()
        var itemIndex = 0
        for (i in 0..<this.keyList.size){
            itemIndex = i
            if (this.keyList[i] == taskName) { break }
        }
        println("$taskName has been added at index $itemIndex.")
    }

    fun removeItem() {}

    fun saveData() {}

}