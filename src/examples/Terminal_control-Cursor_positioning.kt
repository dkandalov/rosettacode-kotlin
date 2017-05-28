package `terminal_control_cursor_positioning`

// version 1.1.2

fun main(args: Array<String>) {
    print("\u001Bc") // clear screen first
    println("\u001B[6;3HHello")
}