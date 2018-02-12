package `reverse_words_in_a_string`

// version 1.0.6

fun reversedWords(s: String) = s.split(" ").reversed().joinToString(" ")

fun main(args: Array<String>) {
    val s = "Hey you, Bub!"
    println(reversedWords(s))
    println()
    val sl = listOf(
        " ---------- Ice and Fire ------------ ", 
        "                                      ",
        " fire, in end will world the say Some ", 
        " ice. in say Some                     ", 
        " desire of tasted I've what From      ", 
        " fire. favor who those with hold I    ",  
        "                                      ",  
        " ... elided paragraph last ...        ", 
        "                                      ",  
        " Frost Robert ----------------------- "
    ) 
    sl.forEach { println(reversedWords(it).trimStart()) }
}