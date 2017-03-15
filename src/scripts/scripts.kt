package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.get
import khttp.post
import khttp.responses.Response
import khttp.structures.cookie.CookieJar
import scripts.EditPageUrl.Companion.asFileName
import scripts.EditPageUrl.Companion.asPackageName
import scripts.LocalCodeSnippet.Companion.postfixedWith
import scripts.LocalCodeSnippet.Companion.trimPackage
import java.io.File
import java.net.URLEncoder
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors
import java.util.stream.Stream

private val excludedTasks = listOf(
    "Boolean_values", // ignored because there is no code
    "Interactive_programming" // ignored because there is no code
)

fun pushLocalChangesToRosettaCode() {
    val snippetStorage = loadCodeSnippets(excludedTasks)

    snippetStorage.snippetsWithDiffs.apply {
        if (isEmpty()) return log(">>> Nothing to push. There are no differences between code in local files and rosetta code website.")

        var cookieJar = cached("loginCookieJar") { loginAndGetCookies() }
        if (cookieJar.hasExpiredEntries()) {
            log("Login cookies has expired entries. Please login to RosettaCode website again.")
            cookieJar = cached("loginCookieJar", replace = true) { loginAndGetCookies() }
        }
        if (cookieJar.isEmpty()) return

        forEach { (webSnippet, localSnippet) ->
            val response = webSnippet.submitCodeChange(localSnippet.sourceCode, cookieJar)
            if (response.statusCode == 200) {
                log("Pushed local changes to ${webSnippet.editPageUrl}")
            } else {
                log("Failed to push local changes to ${webSnippet.editPageUrl} Status code: ${response.statusCode}")
            }
        }
        clearLocalWebCache()
    }

    snippetStorage.onlyLocalSnippets.apply {
        if (isEmpty()) return
        log("Pushing newly create tasks to RosettaCode is currently not supported.\n" +
            "Therefore, you might want to add the following files manually.")
        forEach { log(it.filePath) }
    }
}

private fun CookieJar.hasExpiredEntries(now: Instant = Instant.now()): Boolean {
    return entries
        .map { it.value.split("; ").find { it.startsWith("expires=") } }
        .filterNotNull()
        .any {
            val s = it.replace("expires=", "").replace("-", " ")
            val dateTime = OffsetDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME)
            // Exclude year 1970 because there are some cookies which have timestamp just after 1970
            dateTime.year > 1970 && dateTime.toInstant() <= now
        }
}

private fun loginAndGetCookies(): CookieJar {
    val (userName, password) = showLoginDialog() ?: return CookieJar()
    if (userName.isNullOrEmpty() || password.isNullOrEmpty()) {
        log("Please specify non-empty user name and password to be able to login into RosettaCode website.")
        return CookieJar()
    }
    return LoginPage.get().login(userName, password)
}

fun pullFromRosettaCodeWebsite() {
    val snippetStorage = loadCodeSnippets(excludedTasks)

    snippetStorage.onlyWebSnippets.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which only exist on rosetta code website.\n" +
                "They will be downloaded. If they compile ok, please add them to git repository.")
            forEach {
                val codeSnippet = LocalCodeSnippet.create(it)
                log("Saved source code to ${codeSnippet.filePath}")
            }
        } else {
            log(">>> All tasks from rosetta code website exist in local files.")
        }
    }

    snippetStorage.onlyLocalSnippets.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which only exist locally.\n" +
                "It might be because you just added a task or someone removed task from the website.\n" +
                "See the list of files below. Please make necessary changes to keep repository in sync with website.")
            forEach { log(it.filePath) }
        } else {
            log(">>> All tasks from local files exist on rosetta code website.")
        }
    }

    snippetStorage.snippetsWithDiffs.apply {
        if (isNotEmpty()) {
            log(">>> There are some tasks which have different source code locally and on rosetta code website.\n" +
                "Please make necessary changes to keep repository in sync with website.\n")
            forEach { log("Differences between ${it.second.filePath}\nand ${it.first.editPageUrl}\n") }
        } else {
            log(">>> There are no differences between code in local files and rosetta code website.")
        }
    }
}


fun loadCodeSnippets(exclusions: List<String>): CodeSnippetStorage {
    val kotlinPage = cached("kotlinPage") { LanguagePage.get() }
    val editPageUrls = cached("editPageUrls") {
        kotlinPage.extractTaskPageUrls().mapParallelWithProgress { url, progress ->
            log("Getting edit page url from $url ($progress)")
            TaskPage.get(url).extractKotlinEditPageUrl()
        }
    }

    val editPages = cached("editPages") {
        editPageUrls.mapParallelWithProgress { it, progress ->
            log("Getting source code from $it ($progress)")
            EditPage.get(it)
        }
    }

    val webCodeSnippets = editPages
            .flatMap { it.extractCodeSnippets() }
            .filter { (editPageUrl) ->
                exclusions.none { editPageUrl.value.contains(it) }
            }

    val localCodeSnippets = File("src").listFiles()
            .filter { !it.isDirectory && it.extension == "kt" }
            .map{ LocalCodeSnippet(it.path) }

    return CodeSnippetStorage(webCodeSnippets, localCodeSnippets)
}


data class CodeSnippetStorage(val webSnippets: List<WebCodeSnippet>, val localSnippets: List<LocalCodeSnippet>) {
    val onlyLocalSnippets: List<LocalCodeSnippet>
        get() = localSnippets.filter { local -> webSnippets.none{ local.id == it.id } }

    val onlyWebSnippets: List<WebCodeSnippet>
        get() = webSnippets.filter{ web -> localSnippets.none{ web.id == it.id } }

    val snippetsWithDiffs: List<Pair<WebCodeSnippet, LocalCodeSnippet>>
        get() = webSnippets
            .map{ web ->
                val local = localSnippets.find { web.id == it.id }
                if (local != null) Pair(web, local) else null
            }
            .filterNotNull()
            .filter{ (web, local) -> web.sourceCode != local.sourceCode }
}

class FailedToLogin(message: String) : RuntimeException(message)

data class LoginPage(val html: String, val cookies: CookieJar) {
    fun login(userName: String, password: String): CookieJar {
        if (html.isLoggedIn()) return cookies
        if (html.contains("I'm not a robot")) throw FailedToLogin("Can't login right now because login page has 'I'm not a robot' capture.")

        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        val response = post(
                url = "http://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code",
                data = mapOf(
                        "wpName" to userName,
                        "wpPassword" to password,
                        "wpLoginAttempt" to "Log in",
                        "wpLoginToken" to loginToken
                ),
                cookies = cookies + mapOf("rosettacodeUserName" to URLEncoder.encode(userName, "UTF-8"))
        )
        if (!response.text.isLoggedIn()) throw FailedToLogin("statusCode = ${response.statusCode}")
        return response.cookies
    }

    companion object {
        fun get(url: String = "http://rosettacode.org/wiki/Special:UserLogin") = khttp.get(url).run {
            LoginPage(text, cookies)
        }

        fun String.isLoggedIn() = !contains(">Log in<") && contains(">Log out<") // Checking for "Log in" link in the top right corner of the web page.
    }
}

data class LocalCodeSnippet(val filePath: String) {
    val id = CodeSnippetId(File(filePath).name.replace(".kt", ""))
    val sourceCode
        get() = File(filePath).readText().trimPackage()

    companion object {
        fun create(codeSnippet: WebCodeSnippet): LocalCodeSnippet = codeSnippet.run {
            val sourceCode = sourceCode.trim().let {
                if (it.startsWith("package ")) it
                else "package ${codeSnippet.snippetPackageName()}\n\n" + it
            }
            val localFile = File("src/${localFileName(editPageUrl, index)}")
            localFile.parentFile.mkdirs()
            localFile.writeText(sourceCode)
            LocalCodeSnippet(localFile.path)
        }

        private fun localFileName(editPageUrl: EditPageUrl, index: Int) = editPageUrl.pageId().asFileName().postfixedWith(index) + ".kt"

        private fun WebCodeSnippet.snippetPackageName(): String {
            val packageName = editPageUrl.pageId().asPackageName()
            val postfix = if (index == 0) "" else "_$index" // Add postfix to avoid name conflicts within the same task.
            return "`$packageName$postfix`" // Wrap in "`" so that (almost) any string is still a valid package name.
        }

        fun String.postfixedWith(index: Int): String = if (index == 0) this else "$this-$index"

        fun String.trimPackage() = trim().replace(Regex("^package .*\n"), "").trim()
    }
}

data class CodeSnippetId(val value: String)

data class WebCodeSnippet(val editPageUrl: EditPageUrl, val sourceCode: String, val index: Int) {
    val id = CodeSnippetId(editPageUrl.pageId().asFileName().postfixedWith(index))

    fun submitCodeChange(newCode: String, cookieJar: CookieJar): Response {
        val editPage = EditPage.get(editPageUrl, cookieJar)
        return editPage.submitCodeChange(newCode, index, cookieJar)
    }

    override fun toString(): String {
        return "$editPageUrl - $index"
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
    fun pageId(): String {
        return value.replace(Regex(".*title="), "").replace(Regex("&action.*"), "")
    }

    override fun toString() = value

    companion object {
        /**
         * Replace some characters because they are not "friendly" to be used as file name.
         */
        fun String.asFileName() = replace("/", "-").replace("%27", "").replace("%2B", "-plus-")

        /**
         * Lowercase because it is "kind of java convention".
         * Replace "-" because of https://youtrack.jetbrains.com/issue/KT-15288
         */
        fun String.asPackageName() = asFileName().toLowerCase().replace("-", "_")
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

data class EditPage(val url: EditPageUrl, val html: String) {
    fun extractKotlinSource(): List<String> {
        val html = textAreaContent().unEscapeXml()
        return html.sourceCodeRanges().map{ html.substring(it) }
    }

    private fun String.sourceCodeRanges(startFrom: Int = 0): List<IntRange> {
        val i1 = find(startFrom, 20) { s -> openingTags.any{ tag -> s.startsWith(tag) }} ?: return emptyList()
        val i2 = find(i1, ">") ?: return emptyList()
        val i3 = find(i2, closingTag) ?: return emptyList()

        return listOf(IntRange(i2 + 1, i3 - 1)) + sourceCodeRanges(i3 + 1)
    }

    fun extractCodeSnippets() = extractKotlinSource().mapIndexed { index, code ->
        WebCodeSnippet(url, code.trimPackage(), index)
    }

    fun submitCodeChange(newCode: String, index: Int, cookieJar: CookieJar): Response {
        fun String.valueOfTag(tagName: String): String {
            val i1 = indexOf("name=\"$tagName\"")
            if (i1 == -1) return ""
            var i2 = lastIndexOf("value=\"", i1)
            if (i2 == -1) return "" else i2 += "value=\"".length
            val i3 = indexOf("\"", i2)
            if (i3 == -1) return ""
            return substring(i2, i3)
        }

        val updatedContent = textAreaContent().unEscapeXml().run {
            val ranges = sourceCodeRanges()
            if (index <= ranges.size - 1) replaceRange(ranges[index], newCode)
            else this + newCode
        }.escapeXml()

        val section = html.valueOfTag("wpSection")
        val startTime = html.valueOfTag("wpStarttime")
        val editTime = html.valueOfTag("wpEdittime")
        val changeSummary = "/* {{header|Kotlin}} */ Updated example see https://github.com/dkandalov/rosettacode-kotlin for details"
        val parentRevId = html.valueOfTag("parentRevId") 
        val oldId = html.valueOfTag("oldid")
        val autoSummary = html.valueOfTag("wpAutoSummary")
        val editToken = html.valueOfTag("wpEditToken")

        return khttp.post(
            url = "https://rosettacode.org/mw/index.php?title=${url.pageId()}&action=submit",
            cookies = cookieJar,
            data = mapOf(
                    "wpAntispam" to "",
                    "wpSection" to section,
                    "wpStarttime" to startTime,
                    "wpEdittime" to editTime,
                    "wpAutoSummary" to autoSummary,
                    "oldId" to oldId,
                    "parentRevId" to parentRevId,
                    "format" to "text/x-wiki",
                    "model" to "wikitext",
                    "wpTextbox1" to updatedContent,
                    "wpSummary" to changeSummary,
                    "wpSave" to "Save page",
                    "wpEditToken" to editToken,
                    "wpUltimateParam" to "1"
            )
        )
    }

    fun textAreaContent(): String {
        val i1 = html.find(0, "id=\"wpTextbox1\"") ?: return ""
        val i2 = html.find(i1, ">") ?: return ""
        val i3 = html.find(i2, "</textarea>") ?: return ""
        return html.substring(i2 + 1, i3)
    }

    private fun String.find(from: Int, s: String): Int? {
        val i = indexOf(s, from)
        return if (i == -1) null else i
    }

    private fun String.find(from: Int, viewSize: Int, predicate: (String) -> Boolean): Int? {
        return from.until(length).find { i ->
            val endIndex = i + viewSize
            predicate(substring(i, Math.min(endIndex, length)))
        }
    }

    private fun String.unEscapeXml() = replace("&lt;", "<").replace("&amp;", "&")
    private fun String.escapeXml() = this // Don't really escape anything because media wiki can do it.

    companion object {
        private val openingTags = listOf("<lang Kotlin>", "<lang kotlin>", "<lang scala>")
        private val closingTag = "</lang>"

        fun get(url: EditPageUrl, cookieJar: CookieJar = CookieJar()) = EditPage(url, get(url.value, cookies = cookieJar).text)
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
fun <T> cached(id: String, replace: Boolean = false, f: () -> T): T {
    val file = File(".cache/$id.xml")
    if (file.exists() && !replace) {
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

fun clearLocalWebCache(vararg excluding: String = emptyArray()) {
    val cacheDir = ".cache"
    log(">>> Deleting files from $cacheDir")
    File(cacheDir).listFiles().filterNot { excluding.contains(it.name) }.forEach {
        val wasDeleted = it.delete()
        if (wasDeleted) {
            log("Deleted ${it.name}")
        } else {
            log("Failed to delete ${it.name}")
        }
    }
}

data class Progress(val i: Int, val total: Int) {
    fun next() = copy(i = i + 1)
    override fun toString() = "$i of $total"
}

private fun <T, R> List<T>.mapParallelWithProgress(f: (T, Progress) -> R): List<R> {
    val progressRef = AtomicReference(Progress(0, size))
    val poolSize = 20 // No particular reason for this number. Overall, the process is implied to be IO-bound.
    return ForkJoinPool(poolSize).submit(Callable {
        parallelStream().map {
            val progress = progressRef.updateAndGet { it.next() }
            f(it, progress)
        }.toList()
    }).get()
}

private fun <E> Stream<E>.toList(): List<E> {
    return collect(Collectors.toList<E>())
}