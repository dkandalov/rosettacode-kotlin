package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import java.io.File
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun main(args: Array<String>) {
    val codeEntries =
        cached("codeEntries") {
            EditPageUrlsLoader.load()
                .toParallelStream().flatMap{ CodeSnippet.create(it).toStream() }
                .collect(Collectors.toList<CodeSnippet>())
        }
        .filter { codeSnippet ->
            exclusions.none{ codeSnippet.editPageUrl.contains(it) }
        }

    codeEntries
        .filter{ !it.sourceCodeExistsLocally() }
        .forEach{ it.writeCodeToLocalFile() }

    codeEntries
        .filter{ it.sourceCodeExistsLocally() && it.localSourceCodeIsDifferentFromWeb() }
        .forEach{ log("Source code has differences at snippet index: ${it.index}; url: ${it.editPageUrl}") }
}

val exclusions = listOf(
    "Boolean_values", // ignore because there is no code
    "Create_a_two-dimensional_array_at_runtime", // https://youtrack.jetbrains.com/issue/KT-15196
    "Catalan_numbers", // net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps.*
    "Read_a_configuration_file" // doesn't compile
)

data class CodeSnippet(val editPageUrl: String, val sourceCodeOnWeb: String, val index: Int) {
    private val localFile = localFile()
    private val snippetPackageName = snippetPackageName()

    fun writeCodeToLocalFile() {
        localFile.parentFile.mkdirs()

        val sourceCode = sourceCodeOnWeb.let {
            if (it.trim().startsWith("package ")) it
            else "package $snippetPackageName\n\n" + it
        }
        localFile.writeText(sourceCode)

        log("Saved source code to $localFile")
    }

    fun sourceCodeExistsLocally(): Boolean {
        return localFile.exists()
    }

    fun localSourceCodeIsDifferentFromWeb(): Boolean {
        fun String.trimmedLines() = replace("package $snippetPackageName\n", "")
                .trim().split("\n").map(String::trim)
        return localFile.readText().trimmedLines() != sourceCodeOnWeb.trimmedLines()
    }

    private fun localFile(): File {
        val fileName = editPageUrl.extractPageName()
        val postfix = if (index == 0) "" else "-$index"
        return File("src/$fileName$postfix.kt")
    }

    private fun snippetPackageName(): String {
        val postfix = if (index == 0) "" else "_$index"
        return editPageUrl.extractPageName().replace("-", "_").toLowerCase() + postfix
    }

    override fun toString(): String {
        return "$editPageUrl - $index"
    }

    companion object {
        fun create(editPageUrl: String): List<CodeSnippet> {
            val codeSnippets = fetchCodeSnippets(editPageUrl)
            return codeSnippets.mapIndexed { i, it -> CodeSnippet(editPageUrl, it, i) }
        }

        private fun String.extractPageName(): String {
            return replace(Regex(".*title="), "").replace(Regex("&action.*"), "").replace("/", "-").replace("%27", "")
        }

        private fun fetchCodeSnippets(editPageUrl: String): List<String> {
            log("Fetching source code from $editPageUrl")
            return get(editPageUrl).text.split("\n").extractKotlinSource()
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

data class LanguagePage(val html: String) {
    fun extractProblemPageUrls(): List<String> {
        val linesWithHref = html.split("\n")
                .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
                .takeWhile { !it.contains("<div class=\"printfooter\">") }
                .filter { it.contains("href") }

        return linesWithHref.map{ extractUrl(it) }
    }

    private fun extractUrl(it: String): String {
        val s = it.replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
        return "http://rosettacode.org/$s"
    }

    companion object {
        fun get(url: String = "http://rosettacode.org/wiki/Category:Kotlin"): LanguagePage {
            return LanguagePage(khttp.get(url).text)
        }
    }
}

object EditPageUrlsLoader {
    fun load(): List<String> {
        val kotlinPage = cached("kotlinPage") { LanguagePage.get() }
        return cached("editPageUrls") {
            kotlinPage.extractProblemPageUrls().map {
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

    private fun extractUrl(it: String): String {
        val s = it.replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
        return "http://rosettacode.org/$s"
    }
}

val log: (Any?) -> Unit = {
    System.out.println(it)
}

private val xStream = XStream(XppDriver())

/**
 * Caches result of function `f` in xml file.
 * The main reason for this is to speed up execution.
 *
 * To "invalidate" cached value modify or remove xml file.
 */
fun <T> cached(id: String, f: () -> T): T {
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


// Need this because default java methods don't work in kotlin 1.0.5
private fun <E> Collection<E>.toStream(): Stream<E> {
    return StreamSupport.stream(Spliterators.spliterator<E>(this, 0), false)
}

// Need this because default java methods don't work in kotlin 1.0.5
private fun <E> Collection<E>.toParallelStream(): Stream<E> {
    return StreamSupport.stream(Spliterators.spliterator<E>(this, 0), true)
}