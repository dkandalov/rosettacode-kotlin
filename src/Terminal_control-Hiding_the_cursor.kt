package `terminal_control_hiding_the_cursor`

// version 1.1.1

fun main(args: Array<String>) {
    print("\u001B[?25l")      // hide cursor
    Thread.sleep(2000)        // wait 2 seconds before redisplaying cursor
    print("\u001B[?25h")      // display cursor
    Thread.sleep(2000)        // wait 2 more seconds before exiting
}