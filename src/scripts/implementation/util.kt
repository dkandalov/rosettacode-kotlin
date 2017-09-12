package scripts.implementation

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import org.http4k.client.ApacheClient
import org.http4k.core.Parameters
import org.http4k.core.Request
import org.http4k.core.body.form
import org.http4k.core.body.toBody
import org.http4k.core.cookie.Cookie
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

val log: (Any?) -> Unit = {
    System.out.println(it)
}

fun newHttpClient() = ApacheClient()

fun newRecordingHttpClient(recordedRequestsMatcher: (Request) -> Boolean = { false }): RecordingHttpClient {
    val file = File(".cache/httpRecording.xml")
    val recording = if (file.exists()) xStream.fromXML(file.readText()) as HttpRecording else HttpRecording()

    val recordingHttpClient = RecordingHttpClient(newHttpClient(), recording.filter(recordedRequestsMatcher))

    Runtime.getRuntime().addShutdownHook(Thread {
        file.parentFile.mkdirs()
        file.writeText(xStream.toXML(recordingHttpClient.recording))
    })
    return recordingHttpClient
}

private val xStream = XStream(XppDriver())

data class Cookies(val list: List<Cookie> = emptyList()) {
    fun anyIsExpired(now: Instant = Instant.now()): Boolean {
        return list
            .mapNotNull { it.value.split("; ").find { it.startsWith("expires=") } }
            .any {
                val s = it.replace("expires=", "").replace("-", " ")
                val dateTime = OffsetDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME)
                // Exclude year 1970 because there are some cookies which have timestamp just after 1970
                dateTime.year > 1970 && dateTime.toInstant() <= now
            }
    }

    operator fun plus(cookie: Cookie) = Cookies(list + cookie)
}

fun Request.with(cookies: Cookies): Request =
    replaceHeader("Cookie", cookies.list.map { "${it.name}=${it.value}" }.joinToString(""))

fun Request.formData(parameters: Parameters) =
    run { body(form().plus(parameters).toBody()) }

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

fun <T> retry(exceptionClass: KClass<out Exception>, retries: Int = 3, f: () -> T): T {
    return try {
        f()
    } catch (e: Exception) {
        if (retries == 1) throw IllegalStateException("Exceeded amount of retries", e)
        if (e.javaClass.kotlin.isSubclassOf(exceptionClass)) {
            retry(exceptionClass, retries - 1, f)
        } else {
            throw e
        }
    }
}

private fun <T> T.ifNull(defaultValue: T): T = this ?: defaultValue