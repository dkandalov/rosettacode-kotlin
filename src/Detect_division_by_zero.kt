package `detect_division_by_zero`

// version 1.0.5-2

fun divideByZero(x: Int, y:Int): Boolean =
    try {
        x / y
        false
    }
    catch(e: ArithmeticException) {
        true
    }
   
fun main(args: Array<String>) {
    // using 'var' instead of 'val' to suppress compiler 'division by zero' warning
    var x = 1
    var y = 0 
    if (divideByZero(x, y))
        println("Attempted to divide by zero")
    else
        println("$x / $y = ${x / y}")
}