package `bulls_and_cows`

// version 1.1.2

import java.util.Random

const val MAX_GUESSES = 20  // say

fun main(args: Array<String>) {
    val r = Random()
    var num: String
    // generate a 4 digit random number from 1234 to 9876 with no zeros or repeated digits
    do {
        num = (1234 + r.nextInt(8643)).toString()
    } while ('0' in num || num.toSet().size < 4)

    println("All guesses should have exactly 4 distinct digits excluding zero.")
    println("Keep guessing until you guess the chosen number (maximum $MAX_GUESSES valid guesses).\n")
    var guesses = 0
    while (true) {
        print("Enter your guess : ")
        val guess = readLine()!!
        if (guess == num) {
            println("You've won with ${++guesses} valid guesses!")
            return
        }
        val n = guess.toIntOrNull()
        if (n == null)
            println("Not a valid number")
        else if ('-' in guess || '+' in guess)
            println("Can't contain a sign")
        else if ('0' in guess)
            println("Can't contain zero")
        else if (guess.length != 4)
            println("Must have exactly 4 digits")
        else if (guess.toSet().size < 4)
            println("All digits must be distinct")
        else {
            var bulls = 0
            var cows  = 0
            for ((i, c) in guess.withIndex()) {
                if (num[i] == c) bulls++
                else if (c in num) cows++
            }
            println("Your score for this guess:  Bulls = $bulls  Cows = $cows")
            guesses++
            if (guesses == MAX_GUESSES)
                println("You've now had $guesses valid guesses, the maximum allowed")
        }
    }
}