package scripts.implementation

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicReference

val log: (Any?) -> Unit = {
    System.out.println(it)
}

private val xStream = XStream(XppDriver())

/**
 * Caches result of function `f` in xml file.
 * The main reason for this is to speed up execution.
 *
 * To "invalidate" cached value modify or remove xml file.
 */
fun <T> cached(id: String, replace: Boolean = false, f: () -> T): T {
    val file = File(".cache/$id.xml")
    if (file.exists() && !replace) {
        log("// Using cached value of '$id'")
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        log("// Recalculating value of '$id'")
        val result = f()
        file.parentFile.mkdirs()
        file.writeText(xStream.toXML(result))
        return result
    }
}

fun clearLocalWebCache(vararg excluding: String = emptyArray()) {
    val cacheDir = ".cache"
    log(">>> Deleting files from $cacheDir")
    File(cacheDir).listFiles().ifNull(emptyArray()).filterNot { excluding.contains(it.name) }.forEach {
        val wasDeleted = it.delete()
        if (wasDeleted) {
            log("Deleted ${it.name}")
        } else {
            log("Failed to delete ${it.name}")
        }
    }
}

data class Progress(val i: Int, val total: Int) {
    fun next() = copy(i = i + 1)
    override fun toString() = "$i of $total"
}

fun <T, R> List<T>.mapParallelWithProgress(f: (T, Progress) -> R): List<R> {
    val poolSize = 30 // No particular reason for this number. Overall, the process is assumed to be IO-bound.
    val executor = newFixedThreadPool(poolSize)

    val progressRef = AtomicReference(Progress(0, size))
    val futures = this.map{ executor.submit(Callable {
        val progress = progressRef.updateAndGet { it.next() }
        f(it, progress)
    })}
    val result = futures.map { it.get() }
    executor.shutdown()
    return result
}

private fun <T> T.ifNull(defaultValue: T): T = if (this == null) defaultValue else this