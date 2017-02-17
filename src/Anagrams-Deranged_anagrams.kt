package `anagrams_deranged_anagrams`

// version 1.0.6

import java.net.URL
import java.io.InputStreamReader
import java.io.BufferedReader

fun isDeranged(s1: String, s2: String): Boolean {
    for (i in 0 until s1.length) if (s1[i] == s2[i]) return false
    return true
}

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
        if (!anagrams.containsKey(key)) {
            anagrams.put(key, mutableListOf<String>())
            anagrams[key]!!.add(word)
        }
        else {
            var deranged = true
            for (w in anagrams[key]!!) 
                if (!isDeranged(w, word)) {
                   deranged = false
                   break
                }
            if (deranged) {
                anagrams[key]!!.add(word)
                count = Math.max(count, word.length)
            }
        }        
        word = reader.readLine()
    }
    reader.close()
    for (ana in anagrams.values) if (ana.size > 1 && ana[0].length == count) println(ana)
}