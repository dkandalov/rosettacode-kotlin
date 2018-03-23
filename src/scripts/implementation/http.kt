package scripts.implementation

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookies
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ResilienceFilters
import org.http4k.filter.TrafficFilters
import org.http4k.filter.cookie.BasicCookieStorage
import org.http4k.filter.cookie.CookieStorage
import org.http4k.filter.cookie.LocalCookie
import org.http4k.traffic.ReadWriteCache
import scripts.implementation.pages.LanguagePage
import scripts.implementation.pages.LoginPage
import java.io.File
import java.security.MessageDigest
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

fun newHttpClient() = ApacheClient()

fun HttpHandler.withDebug() = DebuggingFilters.PrintRequestAndResponse().then(this)


interface RCClient: HttpHandler {
    val cookieStorage: BasicCookieStorage
    fun removeFromCache(f: (String) -> Boolean)
}


fun HttpHandler.asRCClient(): RCClient {
    val cookieStorage = cached("cookies") { BasicCookieStorage() }

    val httpCache = DiskHttpCache(
        baseDir = "$cacheDir/http",
        shouldIgnore = {
            (it is Request && it.uri == Uri.of(LanguagePage.url)) ||
            (it is Request && it.method == POST) ||
            (it is Request && it.uri == Uri.of(LoginPage.url))
        }
    )

    val retry = Retry.of("retrying", RetryConfig.custom()
        .maxAttempts(3)
        .intervalFunction { attempt -> (attempt * 2).toLong() }
        .build()
    )

    val httpClient = TrafficFilters.ServeCachedFrom(httpCache)
        .then(TrafficFilters.RecordTo(httpCache))
        .then(Cookies(storage = cookieStorage))
        .then(ResilienceFilters.RetryFailures(retry, isError = { !it.status.successful }))
        .then(this)

    return object: RCClient {
        override val cookieStorage = cookieStorage
        override fun invoke(request: Request) = httpClient(request)
        override fun removeFromCache(f: (String) -> Boolean) = httpCache.remove{ f(it.toMessage()) }
    }
}


private class DiskHttpCache(
    private val baseDir: String,
    private val shouldIgnore: (HttpMessage) -> Boolean = { true }
): ReadWriteCache {

    override fun set(request: Request, response: Response) {
        if (shouldIgnore(request) || shouldIgnore(response)) return

        val requestFolder = request.toFolder(baseDir.toBaseFolder())
        request.writeTo(requestFolder)
        response.writeTo(requestFolder)
    }

    override fun get(request: Request): Response? {
        if (shouldIgnore(request)) return null

        return File(request.toFolder(baseDir.toBaseFolder()), "response.txt").run {
            if (exists()) Response.parse(String(readBytes())) else null
        }
    }

    fun remove(f: (HttpMessage) -> Boolean) {
        baseDir.toBaseFolder().listFiles()
            .filter { it.isDirectory }
            .forEach { dir ->
                val request = File(dir, "request.txt").let { if (it.exists()) Request.parse(it.readText()) else null }
                val response = File(dir, "response.txt").let { if (it.exists()) Response.parse(it.readText()) else null }
                if ((request != null && f(request)) || (response != null && f(response))) {
                    dir.deleteRecursively()
                }
            }
    }

    private fun String.toBaseFolder(): File = File(if (isEmpty()) "." else this)

    private fun Request.toFolder(baseDir: File) =
        File(File(baseDir, uri.path), String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA1").digest(toString().toByteArray())))
            .replace(File.separatorChar, '_'))

    private fun HttpMessage.writeTo(folder: File) {
        toFile(folder).apply {
            folder.mkdirs()
            createNewFile()
            writeBytes(this@writeTo.toString().toByteArray())
        }
    }

    private fun HttpMessage.toFile(folder: File): File = File(folder, if (this is Request) "request.txt" else "response.txt")
}


private object Cookies {
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