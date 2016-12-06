package rosettacode

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import java.io.File

fun main(args: Array<String>) {
    val urls = cached("urls") {
        get("http://rosettacode.org/wiki/Category:Kotlin").text.extractPageUrls()
    }
    val editUrls = cached("editUrls") {
        urls.map { get(it).text.extractKotlinEditUrl() }
    }

    editUrls.drop(0).take(10)
        .forEach { url ->
            val file = File("src/rosettacode/" + url.extractPageName() + ".kt")
            if (!file.exists()) {
                val sourceCode = get(url).text.extractKotlinSource()
                if (sourceCode.trim().isEmpty()) {
                    throw IllegalStateException(url)
                }
                file.writeText(sourceCode)
            }
        }
}

private fun String.extractPageName(): String {
    return this.replace(Regex(".*title="), "").replace(Regex("&action.*"), "").replace("/", "-")
}

private fun <T> cached(id: String, f: () -> T): T {
    val xStream = XStream(XppDriver())
    val file = File(id + ".xml")
    if (file.exists()) {
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        val result = f()
        file.writeText(xStream.toXML(result))
        return result
    }
}

private fun String.extractKotlinSource(): String {
    return split("\n")
        .map{ it.replace("&lt;", "<").replace("&amp;", "&") }
        .dropWhile { !it.contains("<lang Kotlin>") && !it.contains("<lang kotlin>") && !it.contains("<lang scala>") }
        .dropLastWhile { !it.contains("</lang>") }
        .map { it.replace("<lang Kotlin>", "").replace("<lang kotlin>", "").replace("<lang scala>", "").replace("</lang>", "") }
        .joinToString("\n")
}

private fun String.extractKotlinEditUrl(): String {
    val s = split("\n")
        .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
        .find { it.contains("<a href=\"/mw/") }!!
    return extractUrl(s).replace("&amp;", "&")
}

private fun String.extractPageUrls(): List<String> {
    val linesWithHref = split("\n")
            .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
            .takeWhile { !it.contains("<div class=\"printfooter\">") }
            .filter { it.contains("href") }

    return linesWithHref.map(::extractUrl)
}

private fun extractUrl(it: String): String {
    val s = it.replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
    return "http://rosettacode.org/$s"
}