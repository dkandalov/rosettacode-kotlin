package `strip_a_set_of_characters_from_a_string`

// version 1.0.6

fun stripChars(s: String, r: String) = s.replace(Regex("[$r]"), "")

fun main(args: Array<String>) {
    println(stripChars("She was a soul stripper. She took my heart!", "aei"))
}