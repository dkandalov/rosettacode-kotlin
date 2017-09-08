package scripts.implementation

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request

data class LanguagePage(val html: String) {
    fun extractTaskPageUrls(): List<String> {
        val linesWithHref = html.split("\n")
            .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
            .takeWhile { !it.contains("<div class=\"printfooter\">") }
            .filter { it.contains("href") }

        return linesWithHref.map { it.extractUrl() }
    }

    companion object {
        fun getWith(httpClient: HttpHandler, url: String = "http://rosettacode.org/wiki/Category:Kotlin") =
            LanguagePage(httpClient(Request(GET, url)).bodyString())
    }
}