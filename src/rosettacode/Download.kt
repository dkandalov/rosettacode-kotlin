package rosettacode

import khttp.get

fun main(args: Array<String>) {
    val urls = get("http://rosettacode.org/wiki/Category:Kotlin").text.extractPageUrls()

    urls.take(5)
        .map { get(it).text.extractKotlinEditUrl() }
        .forEach { println(get(it).text.extractKotlinSource()) }
}

private fun String.extractKotlinSource(): String {
    return split("\n")
        .map{ it.replace("&lt;", "<") }
        .dropWhile { !it.contains("<lang Kotlin>") }
        .takeWhile { !it.contains("</textarea>") }
        .map { it.replace("<lang Kotlin>", "").replace("</lang>", "") }
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