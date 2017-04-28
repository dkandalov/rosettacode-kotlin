package `hello_world_web_server`

// version 1.1.1

import java.net.ServerSocket
import java.io.PrintWriter

fun main(args: Array<String>) {
    val listener = ServerSocket(8080)
    while(true) {
        val sock = listener.accept()
        PrintWriter(sock.outputStream, true).println("Goodbye, World!")
        sock.close()
    }
}