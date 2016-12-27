package `even_or_odd`

// version 1.0.5-2

fun main(args: Array<String>) {
    var n: Int
    while (true) {
        print("Enter an integer or 0 to finish : ")
        n = readLine()!!.toInt()
        when {
             n == 0      -> return
             n % 2 == 0  -> println("Your number is even")
             else        -> println("Your number is odd")
        }
    }
}