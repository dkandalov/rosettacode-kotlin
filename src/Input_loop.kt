package `input_loop`

// version 1.0.6

import java.util.Scanner

fun main(args: Array<String>) {
    println("Keep entering text or the word 'quit' to end the program:")
    val sc = Scanner(System.`in`)
    var words = mutableListOf<String>()
    var input: String
    while (true) {
        input = sc.next()
        if (input.trim().toLowerCase() == "quit") {
            if (words.size > 0) println("\nYou entered the following words:\n${words.joinToString("\n")}")
            return
        }
        words.add(input)
    }
}