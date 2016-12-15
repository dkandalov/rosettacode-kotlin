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
    val exclusions = listOf(
            "Boolean_values", // ignore because there is no code
            "Create_a_two-dimensional_array_at_runtime", // https://youtrack.jetbrains.com/issue/KT-15196
            "Read_a_configuration_file" // doesn't compile
    )
    val codeEntries = loadKotlinCodeEntries(exclusions)

    codeEntries.filter{ !it.sourceCodeExistsLocally() }.apply {
        if (isNotEmpty()) {
            log("There are some tasks which only exist on rosetta code website. They will be downloaded.")
            forEach {
                it.writeCodeToLocalFile()
                log("Saved source code to ${it.localFile}")
            }
        } else {
            log("All tasks from rosetta code website exist in local files.")
        }
    }

    codeEntries.filter{ it.sourceCodeExistsLocally() && it.localSourceCodeIsDifferentFromWeb() }.apply {
        if (isNotEmpty()) {
            forEach{ log("Source code has differences at snippet index: ${it.index}; url: ${it.editPageUrl}") }
        } else {
            log("There are no differences between code in local files and rosetta code website.")
        }
    }
}

private fun loadKotlinCodeEntries(exclusions: List<String>): List<CodeSnippet> {
    val kotlinPage = cached("kotlinPage") { LanguagePage.get() }
    val editPageUrls = cached("editPageUrls") {
        kotlinPage.extractTaskPageUrls().parallel().map {
            log("Getting edit page urls from $it")
            TaskPage.get(it).extractKotlinEditUrl()
        }.toList()
    }

    val tasksSourceCode = cached("tasksSourceCode") {
        editPageUrls.parallel().map {
            log("Getting source code from $it")
            Pair(it, get(it).text)
        }.toList()
    }

    val codeSnippets = tasksSourceCode
            .flatMap { CodeSnippet.create(it.first, it.second) }
            .filter { codeSnippet ->
                exclusions.none { codeSnippet.editPageUrl.contains(it) }
            }
    return codeSnippets
}

data class CodeSnippet(val editPageUrl: String, val sourceCodeOnWeb: String, val index: Int) {
    val localFile = localFile()
    private val snippetPackageName = snippetPackageName()

    fun writeCodeToLocalFile() {
        localFile.parentFile.mkdirs()

        val sourceCode = sourceCodeOnWeb.let {
            if (it.trim().startsWith("package ")) it
            else "package $snippetPackageName\n\n" + it
        }
        localFile.writeText(sourceCode)
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
        fun create(editPageUrl: String, editPageText: String): List<CodeSnippet> {
            return EditTaskPage(editPageText)
                    .extractKotlinSource()
                    .mapIndexed { i, it -> CodeSnippet(editPageUrl, it, i) }
        }

        private fun String.extractPageName(): String {
            return replace(Regex(".*title="), "").replace(Regex("&action.*"), "").replace("/", "-").replace("%27", "")
        }
    }
}

data class LanguagePage(val html: String) {
    fun extractTaskPageUrls(): List<String> {
        val linesWithHref = html.split("\n")
                .dropWhile { !it.contains("<h2>Pages in category \"Kotlin\"</h2>") }
                .takeWhile { !it.contains("<div class=\"printfooter\">") }
                .filter { it.contains("href") }

        return linesWithHref.map{ it.extractUrl() }
    }

    companion object {
        fun get(url: String = "http://rosettacode.org/wiki/Category:Kotlin") = LanguagePage(khttp.get(url).text)
    }
}

data class TaskPage(val html: String) {
    internal fun extractKotlinEditUrl(): String {
        val s = html.split("\n")
                .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
                .find { it.contains("<a href=\"/mw/") }!!

        return s.extractUrl().replace("&amp;", "&")
    }

    companion object {
        fun get(url: String): TaskPage = TaskPage(khttp.get(url).text)
    }
}

data class EditTaskPage(val html: String) {
    fun extractKotlinSource(): List<String> {
        return html.split("\n").extractKotlinSource()
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

private fun String.extractUrl(): String {
    val s = replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
    return "http://rosettacode.org/$s"
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
        log("// Using cached value of '$id'")
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        log("// Recalculating value of '$id'")
        val result = f()
        file.writeText(xStream.toXML(result))
        return result
    }
}

// Need this because default java methods don't work in kotlin 1.0.5
private fun <E> Collection<E>.parallel(): Stream<E> {
    return StreamSupport.stream(Spliterators.spliterator<E>(this, 0), true)
}

private fun <E> Stream<E>.toList(): List<E> {
    return collect(Collectors.toList<E>())
}