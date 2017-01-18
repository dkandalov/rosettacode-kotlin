package `keyboard_input_flush_the_keyboard_buffer`

// version 1.0.6

fun main(args: Array<String>) {
    while (System.`in`.available() > 0) System.`in`.read()
    println("Goodbye!")
}