package `babbage_problem`

// version 1.0.6 (Kotlin is under active development and this is the latest stable version)

// All Kotlin programs start from a 'main' function' and comments - like this one - start with a double slash.

fun main(args: Array<String>) { // don't worry what 'args' is as it's not needed here
    // We will start looking at integer numbers from 520 onwards as the square root of 269696 is just under that.
    // Also the latter is an even number and so its square root must be even also.

    // Declare some variables first to hold each number and its square.
    var number: Long = 520        // 'number' is a long integer variable which initially has a value of 520
    var square: Long = 520 * 520  // 'square' is a long integer variable containing number's square

    while (true) {  // keep checking numbers indefinitely until we find one which solves the problem
        // examine the last 6 digits of 'square' by converting it first to a string of digits
        val last6 = square.toString().takeLast(6) 
        // check if 'last6' is equal to "269696"
        if (last6 == "269696") {   // we've found the smallest number which solves the problem!
            println ("The smallest number is $number whose square is $square") // print the answer to the terminal
            return // return from the 'main' function which ends the program
        }
        number = number + 2       // otherwise add 2 to get next even number
        square = number * number  // and square it before the next test
    }
}