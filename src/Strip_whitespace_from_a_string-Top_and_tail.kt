package `strip_whitespace_from_a_string_top_and_tail`

// version 1.0.6

fun main(args: Array<String>) {
    val s = "  \tRosetta Code \r\n"
    println("Untrimmed       => $s")
    println("Left Trimmed    => ${s.trimStart()}") 
    println("Right Trimmed   => ${s.trimEnd()}")
    println("Fully Trimmed   => ${s.trim()}")
}