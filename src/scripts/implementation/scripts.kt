package scripts.implementation

import khttp.structures.cookie.CookieJar
import java.io.File

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
            log("Login cookies have expired entries. Please login to RosettaCode website again.")
            cookieJar = cached("loginCookieJar", replace = true) { loginAndGetCookies() }
        }
        if (cookieJar.isEmpty()) return

        forEach { (webSnippet, localSnippet) ->
            val result = webSnippet.submitCodeChange(localSnippet.sourceCode, cookieJar)
            when (result) {
                is EditPage.SubmitResult.Success -> {
                    log("Pushed local changes to ${webSnippet.editPageUrl}")
                }
                is EditPage.SubmitResult.Failure -> {
                    log("Failed to push local changes to ${webSnippet.editPageUrl} (Reason: ${result.reason})")
                }
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
            forEach { log("Differences between ${it.second.filePath} and\n ${it.first.editPageUrl}\n") }
        } else {
            log(">>> There are no differences between code in local files and rosetta code website.")
        }
    }
}


private fun loadCodeSnippets(exclusions: List<String>): CodeSnippetStorage {
    val kotlinPage = cached("kotlinPage") { LanguagePage.Companion.get() }
    val editPageUrls = cached("editPageUrls") {
        kotlinPage.extractTaskPageUrls().mapParallelWithProgress { url, progress ->
            log("Getting edit page url from $url ($progress)")
            TaskPage.Companion.get(url).extractKotlinEditPageUrl()
        }
    }

    val editPages = cached("editPages") {
        editPageUrls.mapParallelWithProgress { it, progress ->
            log("Getting source code from $it ($progress)")
            EditPage.Companion.get(it)
        }
    }

    val webCodeSnippets = editPages
        .flatMap { it.extractCodeSnippets() }
        .filter { (editPageUrl) ->
            exclusions.none { editPageUrl.value.contains(it) }
        }

    val localCodeSnippets = File("src").listFiles()
        .filter { !it.isDirectory && it.extension == "kt" }
        .map { LocalCodeSnippet(it.path) }

    return CodeSnippetStorage(webCodeSnippets, localCodeSnippets)
}
