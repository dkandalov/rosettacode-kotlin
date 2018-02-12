package `count_occurrences_of_a_substring`

// version 1.0.6

fun countSubstring(s: String, sub: String): Int = s.split(sub).size - 1

fun main(args: Array<String>) {
    println(countSubstring("the three truths","th"))
    println(countSubstring("ababababab","abab"))
    println(countSubstring("",""))
}