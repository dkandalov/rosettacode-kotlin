package `synchronous_concurrency`

// version 1.1.51

import java.util.concurrent.SynchronousQueue
import kotlin.concurrent.thread
import java.io.File

val queue = SynchronousQueue<String>()

const val EOT = "\u0004"  // end of transmission

fun main(args: Array<String>) {
    thread {
        var count = 0

        while (true) {
             val line = queue.take()
             if (line == EOT) {
                queue.put(count.toString())
                break
             }
             println(line)
             count++
        }
    }

    File("input.txt").forEachLine { line -> queue.put(line) }
    queue.put(EOT)
    val count = queue.take().toInt()
    println("\nNumber of lines printed = $count")
}