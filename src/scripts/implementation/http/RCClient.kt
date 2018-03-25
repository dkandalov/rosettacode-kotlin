package scripts.implementation.http

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.filter.ResilienceFilters
import org.http4k.filter.TrafficFilters
import org.http4k.filter.cookie.BasicCookieStorage
import org.http4k.filter.cookie.LocalCookie
import scripts.implementation.cacheDir
import scripts.implementation.cached
import scripts.implementation.pages.LanguagePage
import scripts.implementation.pages.LoginPage
import java.time.LocalDateTime

interface RCClient: HttpHandler {
    fun removeFromCache(f: (String) -> Boolean)
    fun clearCache()
    fun store(cookie: Cookie, time: LocalDateTime = LocalDateTime.now())
}

fun HttpHandler.asRCClient(): RCClient {
    val cookieStorage = cached("cookies") { BasicCookieStorage() }

    val httpCache = DiskHttpCache(
        baseDir = "$cacheDir/http",
        shouldIgnore = {
            (it is Request && it.uri == Uri.of(LanguagePage.url)) ||
                (it is Request && it.method == Method.POST) ||
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
        override fun invoke(request: Request) = httpClient(request)
        override fun removeFromCache(f: (String) -> Boolean) = httpCache.remove { f(it.toMessage()) }
        override fun clearCache() = httpCache.clear()
        override fun store(cookie: Cookie, time: LocalDateTime) =
            cookieStorage.store(listOf(LocalCookie(cookie, time)))
    }
}