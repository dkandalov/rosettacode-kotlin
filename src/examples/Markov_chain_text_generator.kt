package `markov_chain_text_generator`

// version 1.1.51

import java.io.File
import java.util.Random

val r = Random()

fun markov(filePath: String, keySize: Int, outputSize: Int): String {
    if (keySize < 1) throw IllegalArgumentException("Key size can't be less than 1")
    val words = File(filePath).readText().trimEnd().split(' ')
    if (outputSize !in keySize..words.size) {
        throw IllegalArgumentException("Output size is out of range")
    }
    val dict = mutableMapOf<String, MutableList<String>>()

    for (i in 0..(words.size - keySize)) {
        val key = words.subList(i, i + keySize).joinToString(" ")
        val value = if (i + keySize < words.size) words[i + keySize] else ""
        if (!dict.containsKey(key))
            dict.put(key, mutableListOf(value))
        else
            dict[key]!!.add(value)
    }

    val output = mutableListOf<String>()
    var n = 0
    var rn = r.nextInt(dict.size)
    var prefix = dict.keys.toList()[rn]
    output.addAll(prefix.split(' '))

    while (true) {
        var suffix = dict[prefix]!!
        if (suffix.size == 1) {
            if (suffix[0] == "") return output.joinToString(" ")
            output.add(suffix[0])
        }
        else {
            rn = r.nextInt(suffix.size)
            output.add(suffix[rn])
        }
        if (output.size >= outputSize) return output.take(outputSize).joinToString(" ")
        n++
        prefix = output.subList(n, n + keySize).joinToString(" ")
    }
}

fun main(args: Array<String>) {
    println(markov("alice_oz.txt", 3, 200))
}