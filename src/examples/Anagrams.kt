package `anagrams`

// version 1.0.6

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

fun main(args: Array<String>) {
    val url = URL("http://www.puzzlers.org/pub/wordlists/unixdict.txt")
    val isr = InputStreamReader(url.openStream())
    val reader = BufferedReader(isr)
    val anagrams = mutableMapOf<String, MutableList<String>>()
    var count = 0
    var word = reader.readLine()
    while (word != null) {
        val chars = word.toCharArray()
        chars.sort()
        val key = chars.joinToString("")
        if (!anagrams.containsKey(key)) anagrams.put(key, mutableListOf<String>())
        anagrams[key]!!.add(word)
        count = Math.max(count, anagrams[key]!!.size)
        word = reader.readLine()
    }
    reader.close()
    anagrams.values
        .filter { it.size == count }
        .forEach { println(it) }
}