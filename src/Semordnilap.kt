import java.io.File

fun main(args: Array<String>) {
    val words = File("/usr/share/dict/words").readLines().toSet()
    val pairs = words.map { Pair(it, it.reversed()) }
            .filter { it.first < it.second && it.second in words } // avoid dupes+palindromes, find matches
    println("Found ${pairs.size} semordnilap pairs")
    println(pairs.take(5))
}