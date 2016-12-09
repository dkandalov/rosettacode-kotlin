package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import java.io.File

fun main(args: Array<String>) {
    val rosettaCodeEntries = cached("codeEntries") {
        KotlinEditPageUrlsLoader.load().subList(10, 20).map(::CodeEntry)
    }.filter{ !exclusions.contains(it.localFile.name) }

    rosettaCodeEntries
            .filter{ !it.localFile.exists() }
            .forEach { it.downloadCodeToLocalFile() }

    rosettaCodeEntries
            .filter{ it.localFile.exists() }
            .filter { it.localFile.readText().trim() != it.sourceCodeOnWeb.trim() }
            .forEach { log("different: " + it.localFile.name + " -- " + it.url) }
}

val exclusions = listOf(
    "Array_concatenation.kt", // need to be combined into one piece of code or add support for downloading multiple files per problem
    "Associative_array-Creation.kt", // compilation error
    "Associative_array-Iteration.kt" // compilation error
)

data class CodeEntry(val url: String, val localFile: File, val sourceCodeOnWeb: String) {
    constructor(url: String): this(url, newLocalFile(url), fetchSourceCode(url))

    fun downloadCodeToLocalFile() {
        log("Saved source code to $localFile")
        localFile.writeText(sourceCodeOnWeb)
    }

    companion object {
        private fun newLocalFile(url: String) = File("src/" + url.extractPageName() + ".kt")

        private fun String.extractPageName(): String {
            return replace(Regex(".*title="), "").replace(Regex("&action.*"), "").replace("/", "-")
        }

        private fun fetchSourceCode(url: String): String {
            log("Fetching source code from $url")
            return get(url).text.extractKotlinSource()
        }

        private fun String.extractKotlinSource(): String {
            return split("\n")
                .map{ it.replace("&lt;", "<").replace("&amp;", "&") }
                .dropWhile { !it.contains("<lang Kotlin>") && !it.contains("<lang kotlin>") && !it.contains("<lang scala>") }
                .dropLastWhile { !it.contains("</lang>") }
                .map { it.replace("<lang Kotlin>", "").replace("<lang kotlin>", "").replace("<lang scala>", "").replace("</lang>", "") }
                .joinToString("\n")
        }
    }
}

object KotlinEditPageUrlsLoader {
    fun load(): List<String> {
        val urls = cached("urls") {
            get("http://rosettacode.org/wiki/Category:Kotlin").text.extractProblemPageUrls()
        }
        return cached("editPageUrls") {
            urls.map {
                log(it)
                get(it).text.extractKotlinEditUrl()
            }
        }
    }

    private fun String.extractKotlinEditUrl(): String {
        val s = split("\n")
                .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
                .find { it.contains("<a href=\"/mw/") }!!
        return extractUrl(s).replace("&amp;", "&")
    }

    private fun String.extractProblemPageUrls(): List<String> {
        val linesWithHref = split("\n")
                .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
                .takeWhile { !it.contains("<div class=\"printfooter\">") }
                .filter { it.contains("href") }

        return linesWithHref.map{ extractUrl(it) }
    }

    private fun extractUrl(it: String): String {
        val s = it.replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
        return "http://rosettacode.org/$s"
    }
}

val log: (Any?) -> Unit = System.out::println

fun <T> cached(id: String, f: () -> T): T {
    val xStream = XStream(XppDriver())
    val file = File(id + ".xml")
    if (file.exists()) {
        log("Using cached value of '$id'")
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        log("Recalculating value of '$id'")
        val result = f()
        file.writeText(xStream.toXML(result))
        log("Done")
        return result
    }
}
