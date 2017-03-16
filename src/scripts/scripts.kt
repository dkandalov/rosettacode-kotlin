package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import khttp.structures.cookie.CookieJar
import scripts.implementation.*
import java.io.File
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
        clearLocalWebCache(excluding = "loginCookieJar.xml")
    }

    snippetStorage.onlyLocalSnippets.apply {
        if (isEmpty()) return
        log(">>> Pushing newly create tasks to RosettaCode is currently not supported.\n" +
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