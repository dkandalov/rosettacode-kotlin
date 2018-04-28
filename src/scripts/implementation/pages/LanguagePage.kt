package scripts.implementation.pages

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import scripts.implementation.extractUrl

data class LanguagePage(private val html: String) {
    fun extractTaskPageUrls(): List<String> {
        val linesWithHref = html.split("\n")
            .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
            .takeWhile { !it.contains("<div class=\"printfooter\">") }
            .filter { it.contains("href") }

        return linesWithHref.map { it.extractUrl() }
    }

    companion object {
        const val url = "http://rosettacode.org/wiki/Category:Kotlin"
    }
}

fun HttpHandler.getKotlinLanguagePage(url: String = LanguagePage.url) =
    LanguagePage(this(Request(GET, url)).bodyString())