package `echo_server`

// version 1.1.3

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ClientHandler(private val clientSocket: Socket): Runnable {
    private val connectionId: Int

    init {
        connectionId = ++numConnections
        println("Handling connection, #$connectionId")
    }

    override fun run() {
        val pw = PrintWriter(clientSocket.outputStream, true)
        val br = BufferedReader(InputStreamReader(clientSocket.inputStream))
        while (true) {
            val line = br.readLine() ?: break
            println("Received: $line")
            pw.write("$line\n")
            pw.flush()
            if (line == "exit") break
        }
        br.close()
        pw.close()
        clientSocket.close()
        println("Closing connection, #$connectionId")
    }

    private companion object {
        var numConnections = 0
    }
}

fun main(args: Array<String>) {
    val serverSocket = ServerSocket(12321)
    try {
        while (true) {
           Thread(ClientHandler(serverSocket.accept())).start()
        }
    }
    finally {
        serverSocket.close()
        println("Closing server socket")
    }
}