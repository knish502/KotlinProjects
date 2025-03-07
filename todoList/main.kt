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
    do {println("Press 'q' to confirm:")} while (readLine() != "q")
}

fun menu() : Int {
    val menuText = "\nEnter an option:\n" +
                " 0: View to-do list\n" +
                " 1: Mark item as completed\n" +
                " 2: Add item to to-do list\n" +
                " 3: Remove item from to-do list\n" + 
                " 4: Quit the program\n> "
    print(menuText)
    return readLine()?.toInt() ?: -1
}



class DataStruct (val fileName: String) {

    var completionMap: MutableMap<String, Boolean> = mutableMapOf<String, Boolean>()
    var keyList: MutableList<String> = mutableListOf()

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
        for (i in 0..this.keyList.size-1){
            print("$i.")
            if (this.completionMap[this.keyList[i]] ?: false){
                print(" [complete]")
            }
            print(" ${this.keyList[i]}\n")
        }
    }
    
    fun checkOffItem() {
        print("\nEnter the index of the item you wish to check off:\n> ")
        val idx = readLine()?.toInt() ?: -100
        this.completionMap[this.keyList[idx]] = true
        println("You have checked off '${this.keyList[idx]}'")
    }
    
    fun addItem() {}
    
    fun removeItem() {}
    
    fun saveData() {}

}