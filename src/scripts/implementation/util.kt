package scripts.implementation

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import org.http4k.core.Parameters
import org.http4k.core.Request
import org.http4k.core.body.form
import org.http4k.core.body.toBody
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicReference

val log: (Any?) -> Unit = {
    System.out.println(it)
}

val cacheDir = ".cache"

private val xStream = XStream(XppDriver())

fun Request.formData(parameters: Parameters) =
    run { body(form().plus(parameters).toBody()) }

/**
 * Caches result of function `f` in xml file.
 * The main reason for this is to speed up execution.
 *
 * To "invalidate" cached value modify or remove xml file.
 */
fun <T> cached(id: String, replace: Boolean = false, f: () -> T): T {
    val file = File("$cacheDir/$id.xml")
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

data class Progress(val i: Int, val total: Int) {
    fun next() = copy(i = i + 1)
    override fun toString() = "$i of $total"
}

fun <T, R> List<T>.mapParallelWithProgress(f: (T, Progress) -> R): List<R> {
    val poolSize = 30 // No particular reason for this number. Overall, the process is assumed to be IO-bound.
    val executor = newFixedThreadPool(poolSize)

    val progressRef = AtomicReference(Progress(0, size))
    val futures = this.map {
        executor.submit(Callable {
            val progress = progressRef.updateAndGet { it.next() }
            f(it, progress)
        })
    }
    val result = futures.map { it.get() }
    executor.shutdown()
    return result
}

fun <T> T.ifNull(defaultValue: T): T = this ?: defaultValue