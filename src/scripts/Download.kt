package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import java.io.File

fun main(args: Array<String>) {
    val rosettaCodeEntries = cached("codeEntries") {
        KotlinEditPageUrlsLoader.load().drop(90).flatMap{ CodeSnippet.create(it) }
    }.filter{ !exclusions.contains(it.localFile.name) }

    rosettaCodeEntries
        .filter{ !it.localFile.exists() }
        .forEach { it.downloadCodeToLocalFile() }

    rosettaCodeEntries
        .filter{ it.localFile.exists() }
        .filter { it.localFile.readText().trim() != it.sourceCodeOnWeb.trim() }
        .forEach { log("different: " + it.localFile.name + " -- " + it.editPageUrl) }
}

val exclusions = listOf(
    "Array_concatenation.kt", // need to be combined into one piece of code or add support for downloading multiple files per problem
    "Associative_array-Creation.kt", // compilation error
    "Associative_array-Iteration.kt", // compilation error
    "Boolean_values.kt", // because there is no code
    "Create_a_two-dimensional_array_at_runtime.kt", // https://youtrack.jetbrains.com/issue/KT-15196
    "Catalan_numbers.kt", // net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps.*
    "Read_a_configuration_file.kt" // doesn't compile
)

data class CodeSnippet(val editPageUrl: String, val localFile: File, val sourceCodeOnWeb: String, val index: Int) {
    fun downloadCodeToLocalFile() {
        log("Saved source code to $localFile")
        localFile.writeText(sourceCodeOnWeb)
    }

    companion object {
        fun create(url: String): List<CodeSnippet> {
            val codeSnippets = fetchCodeSnippets(url)
            val localFiles = if (codeSnippets.isEmpty()) {
                emptyList<File>()
            } else {
                listOf(File("src/" + url.extractPageName() + ".kt")) +
                    1.rangeTo(codeSnippets.size - 1).map { File("src/${url.extractPageName()}-$it.kt") }
            }
            return localFiles.zip(codeSnippets).mapIndexed { i, it ->
                CodeSnippet(url, it.first, it.second, i)
            }
        }

        private fun String.extractPageName(): String {
            return replace(Regex(".*title="), "").replace(Regex("&action.*"), "").replace("/", "-")
        }

        private fun fetchCodeSnippets(url: String): List<String> {
            log("Fetching source code from $url")
            return get(url).text.split("\n").extractKotlinSource()
        }

        private fun List<String>.extractKotlinSource(): List<String> {
            if (isEmpty()) return emptyList()

            val lines = map { it.replace("&lt;", "<").replace("&amp;", "&") }
                    .dropWhile { !it.contains("<lang Kotlin>") && !it.contains("<lang kotlin>") && !it.contains("<lang scala>") }
            val srcLineCount = lines.indexOfFirst { it.contains("</lang>") } + 1
            if (srcLineCount == 0) return emptyList()

            val sourceCode = lines.take(srcLineCount)
                    .map { it.replace("<lang Kotlin>", "").replace("<lang kotlin>", "").replace("<lang scala>", "").replace("</lang>", "") }
                    .joinToString("\n")

            return listOf(sourceCode) + lines.drop(srcLineCount).extractKotlinSource()
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

val log: (Any?) -> Unit = {
    System.out.println(it)
}

/**
 * Caches result of function `f` in xml file.
 * The main reason for this is to speed up execution.
 *
 * To "invalidate" cached value modify or remove xml file.
 */
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
