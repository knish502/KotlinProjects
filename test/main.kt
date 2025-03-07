fun main(args: Array<String>) {

    /*
    Goal:
        i will continue to increase
        d resets to 0 at some point along the line
        i and d should increase at the same rate, including after d is reset
		the rate of increase is unknown
        
        ex:
        i   d
        --+--
        0   0
        1   1
        2   2
        3   0  <- d resets here
        4   1
        5   2
        6   3
        7   4
     */

    var d = 0
    var diff = 0
    for (i in 0..25) {
        d = i + diff
        if (i == 8) {
            d = 0
            diff = -i
        }
        println("${i} -> ${d}")
    }
}
