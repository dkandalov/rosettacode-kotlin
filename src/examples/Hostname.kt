package `hostname`

// version 1.0.6

import java.net.InetAddress

fun main(args: Array<String>) {
    println(InetAddress.getLocalHost().hostName)
}