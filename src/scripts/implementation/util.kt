package scripts.implementation

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.core.body.toBody
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookies
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.TrafficFilters
import org.http4k.filter.cookie.BasicCookieStorage
import org.http4k.filter.cookie.CookieStorage
import org.http4k.filter.cookie.LocalCookie
import org.http4k.traffic.ReadWriteCache
import java.io.File
import java.time.Clock
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

val log: (Any?) -> Unit = {
    System.out.println(it)
}

interface RCClient : HttpHandler {
    val cookieStorage: BasicCookieStorage
}

fun HttpHandler.asRCClient(): RCClient {
    val cookieStorage = cached("cookies") { BasicCookieStorage() }
    val httpClient = Cookies(storage = cookieStorage).then(this)
    return object : RCClient {
        override val cookieStorage = cookieStorage
        override fun invoke(request: Request) = httpClient(request).also {
            cached("cookies", replace = true) { cookieStorage }
        }
    }
}

fun newHttpClient() = ApacheClient()

fun HttpHandler.withDebug() = DebuggingFilters.PrintRequestAndResponse().then(this)

fun HttpHandler.cached(shouldStore: (HttpMessage) -> Boolean = { true }): HttpHandler {
    val storage = ReadWriteCache.Disk(".cache/http", shouldStore)
    return TrafficFilters.ServeCachedFrom(storage)
        .then(TrafficFilters.RecordTo(storage))
        .then(this)
}


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


object Cookies {
    operator fun invoke(
        clock: Clock = Clock.systemDefaultZone(),
        storage: CookieStorage = BasicCookieStorage()
    ): Filter = Filter { next ->
        { request ->
            val now = clock.now()
            removeExpired(now, storage)
            val response = next(request.withLocalCookies(storage))
            storage.store(response.cookies().map { LocalCookie(it, now) })
            response
        }
    }

    private fun Request.withLocalCookies(storage: CookieStorage) =
        storage.retrieve().map { it.cookie }.fold(this, { r, cookie -> r.cookie(cookie.name, cookie.value) })

    private fun removeExpired(now: LocalDateTime, storage: CookieStorage) =
        storage.retrieve().filter { it.isExpired(now) }.forEach { storage.remove(it.cookie.name) }

    private fun Clock.now() = LocalDateTime.ofInstant(instant(), zone)

    private fun Request.cookie(name: String, value: String): Request = replaceHeader("Cookie", cookies().plus(Cookie(name, value)).toCookieString())

    private fun List<Cookie>.toCookieString() = map { "${it.name}=${it.value}" }.joinToString("; ")
}


private fun <T> T.ifNull(defaultValue: T): T = this ?: defaultValue