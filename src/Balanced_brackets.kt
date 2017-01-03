package `balanced_brackets`

// version 1.0.6

import java.util.*

fun isBalanced(s: String): Boolean {
    if (s.length == 0) return true
    var countLeft: Int = 0  // counts number of left brackets so far unmatched
    for (c in s) {
        if (c == '[')
            countLeft += 1
        else if (countLeft > 0)
            countLeft--
        else
            return false
    } 
    return countLeft == 0
}

fun main(args: Array<String>) {
    // checking examples in task description
    val brackets = arrayOf("", "[]", "][", "[][]", "][][", "[[][]]", "[]][[]") 
    for (b in brackets) {
        print(if (b != "") b else "(empty)")
        println("\t  " + if (isBalanced(b)) "OK" else "NOT OK") 
    }
    println()
    // checking 7 random strings of brackets of length 8 say
    val rand = Random()
    var s: String
    var r: Int
    for (i in 1 .. 7) {
        s = ""
        for (j in 1 .. 8) {
            r = rand.nextInt(2)
            if (r == 0) s += '[' else  s += ']' 
        }
        println("$s  " + if (isBalanced(s)) "OK" else "NOT OK") 
    }   
}