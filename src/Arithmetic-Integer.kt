package `arithmetic_integer`

// version 1.1

fun main(args: Array<String>) {
    val r = Regex("""-?\d+[ ]+-?\d+""")
    while(true) {
        print("Enter two integers separated by space(s) or q to quit: ")
        val input: String = readLine()!!.trim()
        if (input == "q" || input == "Q") break
        if (!input.matches(r)) {
            println("Invalid input, try again")
            continue
        }
        val index = input.lastIndexOf(' ')
        val a = input.substring(0, index).trimEnd().toLong()
        val b = input.substring(index + 1).toLong()
        println("$a + $b = ${a + b}")
        println("$a - $b = ${a - b}")
        println("$a * $b = ${a * b}")
        if (b != 0L) {
            println("$a / $b = ${a / b}")  // rounds towards zero
            println("$a % $b = ${a % b}")  // if non-zero, matches sign of first operand
        }
        else {
            println("$a / $b = undefined")
            println("$a % $b = undefined")
        }
        val d = Math.pow(a.toDouble(), b.toDouble())
        print("$a ^ $b = ")
        if (d % 1.0 == 0.0) {
            if (d >= Long.MIN_VALUE.toDouble() && d <= Long.MAX_VALUE.toDouble())
                println("${d.toLong()}")
            else
                println("out of range")
        }
        else if (!d.isFinite())
            println("not finite")
        else
            println("not integral")
        println()        
    }       
}