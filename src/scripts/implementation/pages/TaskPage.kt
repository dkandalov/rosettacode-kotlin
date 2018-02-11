package scripts.implementation.pages

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import scripts.implementation.extractUrl

data class TaskPage(private val html: String) {
    fun extractKotlinEditPageUrl(): EditPageUrl {
        val s = html.split("\n")
            .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
            .find { it.contains("<a href=\"/mw/") }!!

        return EditPageUrl(s.extractUrl().replace("&amp;", "&"))
    }

    companion object {
        fun getWith(httpClient: HttpHandler, url: String) =
            TaskPage(httpClient(Request(GET, url)).bodyString())
    }
}