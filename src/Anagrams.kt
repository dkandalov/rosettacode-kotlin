package `anagrams`

// version 1.0.6

import java.net.URL
import java.io.InputStreamReader
import java.io.BufferedReader

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
    for (ana in anagrams.values) if (ana.size == count) println(ana)
}