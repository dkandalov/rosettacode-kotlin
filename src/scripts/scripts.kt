package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import khttp.structures.cookie.CookieJar
import java.io.File
import java.util.*
import java.util.stream.*

private val exclusions = listOf(
    "Boolean_values", // ignored because there is no code
    "Interactive_programming" // ignored because there is no code
)

fun syncRepoWithRosettaCodeWebsite() {
    val codeSnippets = loadCodeSnippets(exclusions)

    codeSnippets.filter { !it.existsLocally() }.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which only exist on rosetta code website.\n" +
                "They will be downloaded. If they compile ok, please add them to git repository.")
            forEach {
                it.writeCodeToLocalFile()
                log("Saved source code to ${it.localFilePath}")
            }
        } else {
            log(">>> All tasks from rosetta code website exist in local files.")
        }
    }

    codeSnippets.filter { !it.existsOnWeb() }.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which only exist locally.\n" +
                "It might be because you just added a task or someone removed task from the website.\n" +
                "See the list of files below. Please make necessary changes to keep repository in sync with website.")
            forEach { log("Saved source code to ${it.localFilePath}") }
        } else {
            log(">>> All tasks from local files exist on rosetta code website.")
        }
    }

    codeSnippets.filter { it.existsLocally() && it.existsOnWeb() && it.localCodeIsDifferentFromWeb() }.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which have different source code locally and on rosetta code website.\n" +
                "Please make necessary changes to keep repository in sync with website.")
            forEach { log("Differences at snippet index: ${it.index}; url: ${it.editPageUrl}") }
        } else {
            log(">>> There are no differences between code in local files and rosetta code website.")
        }
    }
}

data class LoginPage(val html: String, val cookies: CookieJar) {
    fun login(userName: String, password: String): CookieJar {
        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        val response = khttp.post(
                url = "http://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code",
                data = mapOf(
                        "wpName" to userName,
                        "wpPassword" to password,
                        "wpLoginAttempt" to "Log in",
                        "wpLoginToken" to loginToken
                ),
                cookies = cookies + mapOf("rosettacodeUserName" to userName.replace(' ', '+')) // TODO use encode
        )
        return response.cookies
    }

    companion object {
        fun get(url: String = "http://rosettacode.org/wiki/Special:UserLogin") = khttp.get(url).let {
            LoginPage(it.text, it.cookies)
        }
    }
}


fun loadCodeSnippets(exclusions: List<String>): List<CodeSnippet> {
    val kotlinPage = cached("kotlinPage") { LanguagePage.get() }
    val editPageUrls = cached("editPageUrls") {
        kotlinPage.extractTaskPageUrls().parallel().map {
            log("Getting edit page url from $it")
            TaskPage.get(it).extractKotlinEditPageUrl()
        }.toList()
    }

    val tasksSourceCode = cached("tasksSourceCode") {
        editPageUrls.parallel().map {
            log("Getting source code from $it")
            Pair(it, get(it.value).text)
        }.toList()
    }

    val codeSnippets = tasksSourceCode
            .flatMap { CodeSnippet.create(it.first, it.second) }
            .filter { codeSnippet ->
                exclusions.none { codeSnippet.editPageUrl.value.contains(it) }
            }

    val localCodeSnippets = File("src").listFiles()
            .filter { !it.isDirectory && it.extension == "kt" }
            .filter { file -> codeSnippets.none { it.localFilePath == file.path } }
            .map{ CodeSnippet(EditPageUrl.none, "", -1, it.path) }

    return codeSnippets + localCodeSnippets
}

data class CodeSnippet(val editPageUrl: EditPageUrl, val sourceCodeOnWeb: String, val index: Int, val localFilePath: String) {
    private val localFile = File(localFilePath)
    private val snippetPackageName = snippetPackageName()

    constructor(editPageUrl: EditPageUrl, sourceCodeOnWeb: String, index: Int):
            this(editPageUrl, sourceCodeOnWeb, index, localFileName(editPageUrl, index))

    fun writeCodeToLocalFile() {
        localFile.parentFile.mkdirs()

        val sourceCode = sourceCodeOnWeb.trim().let {
            if (it.startsWith("package ")) it
            else "package $snippetPackageName\n\n" + it
        }
        localFile.writeText(sourceCode)
    }

    fun existsLocally() = localFile.exists()

    fun existsOnWeb() = editPageUrl != EditPageUrl.none

    fun localCodeIsDifferentFromWeb(): Boolean {
        fun String.trimmedLines() = replace("package $snippetPackageName\n", "")
                .trim().split("\n").map(String::trim)
        return localFile.readText().trimmedLines() != sourceCodeOnWeb.trimmedLines()
    }

    private fun snippetPackageName(): String {
        // Lowercase because it is "kind of java convention".
        // Replace "-" because of https://youtrack.jetbrains.com/issue/KT-15288
        val packageName = editPageUrl.extractPageName().toLowerCase().replace("-", "_")
        val postfix = if (index == 0) "" else "_$index" // Add postfix to avoid name conflicts within the same task.
        return "`$packageName$postfix`" // Wrap in "`" so that (almost) any string is still a valid package name.
    }

    override fun toString(): String {
        return "$editPageUrl - $index"
    }

    companion object {
        fun create(url: EditPageUrl, editPageText: String): List<CodeSnippet> {
            return EditTaskPage(editPageText)
                    .extractKotlinSource()
                    .mapIndexed { i, it -> CodeSnippet(url, it, i) }
        }

        private fun localFileName(editPageUrl: EditPageUrl, index: Int): String {
            val fileName = editPageUrl.extractPageName()
            val postfix = if (index == 0) "" else "-$index"
            return "src/$fileName$postfix.kt"
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

data class EditPageUrl(val value: String) {
    fun extractPageName(): String {
        return value.replace(Regex(".*title="), "")
                .replace(Regex("&action.*"), "")
                .replace("/", "-")
                .replace("%27", "")
                .replace("%2B", "-plus-")
    }

    override fun toString() = value

    companion object {
        val none = EditPageUrl("")
    }
}

data class TaskPage(val html: String) {
    internal fun extractKotlinEditPageUrl(): EditPageUrl {
        val s = html.split("\n")
                .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
                .find { it.contains("<a href=\"/mw/") }!!

        return EditPageUrl(s.extractUrl().replace("&amp;", "&"))
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
    val file = File(".cache/$id.xml")
    if (file.exists()) {
        log("// Using cached value of '$id'")
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        log("// Recalculating value of '$id'")
        val result = f()
        file.parentFile.mkdirs()
        file.writeText(xStream.toXML(result))
        return result
    }
}

fun clearLocalWebCache() {
    val cacheDir = ".cache"
    log(">>> Deleting files from $cacheDir")
    File(cacheDir).listFiles().forEach {
        val wasDeleted = it.delete()
        if (wasDeleted) {
            log("Deleted ${it.name}")
        } else {
            log("Failed to delete ${it.name}")
        }
    }
}

// Need this because default java methods don't work in kotlin 1.0.5
private fun <E> Collection<E>.parallel(): Stream<E> {
    return StreamSupport.stream(Spliterators.spliterator<E>(this, 0), true)
}

private fun <E> Stream<E>.toList(): List<E> {
    return collect(Collectors.toList<E>())
}