package `string_matching`

// version 1.0.6

fun main(args: Array<String>) {
    val s1 = "abracadabra"
    val s2 = "abra"    
    println("$s1 begins with $s2 : ${s1.startsWith(s2)}")
    println("$s1 ends with $s2   : ${s1.endsWith(s2)}")
    val b  = s2 in s1
    print("$s1 contains $s2    : $b")
    if (b) println(" at locations ${s1.indexOf(s2) + 1} and ${s1.lastIndexOf(s2) + 1}")
    else println()
}