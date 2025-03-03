

fun main(args: Array<String>)
{
    val taxRate = 0.4
    val children = 3
    
    val assets = mapOf(
        "beachHouse" to 1500000.0,
        "home" to 1500000.0,
        "KCar1" to 50000.0,
        "KCar2" to 250000.0,
        "MCar1" to 75000.0,
        "MCar2" to 50000.0
    )

    val lifeStyleExpenses = mapOf(
        "annualTripCost" to 10000.0,
        "annualSpendingCost" to 10000.0,
        "annualDiningCosts" to 14000.0
    )

    
    val costOfLivingExpenses = mapOf(
        "assetCarryCost" to (mapSum(assets) * 0.015),
        "healthInsurance" to 10000.0,
        "homeInsurance" to 350000.0,
        "carInsurance" to 3500.0,
        "lifestyleExpenses" to mapSum(lifeStyleExpenses),
        "annualLivingExpenses" to 10000.0,
        "childrenCost" to (children * 15000.0)
    )
    

    // calculate ptea (pretax earning amount)
    val ptea = mapSum(assets) / (1 - taxRate)
    println("Pre-tax Earning Amount (PTEA): ${String.format("%.2f", ptea)}")

    // calculate acc (annual carrying costs):
    val pretaxACC = mapSum(costOfLivingExpenses)
    val acc = pretaxACC / (1 - taxRate)
    println("Annual Carrying Costs (ACC): ${String.format("%.2f", acc)}")

    // calculate money system
    val moneySystem = acc/0.05
    println("Money System: ${String.format("%.2f", moneySystem)}")

    // calculate pom (peace-of-mind variable)
    val pom = moneySystem/(2 * taxRate)
    println("Peace of Mind variable (POM): ${String.format("%.2f", pom)}")

    val ESCAPE_NUMBER = ptea + moneySystem + pom
    println("\nEscape number: ${String.format("%.2f", ESCAPE_NUMBER)}")

}

fun mapSum(inMap: Map<String, Double>): Double
{
    var sum = 0.0
    for (value in inMap.values)
    {
        sum = sum + value
    }

    return (sum)
}

