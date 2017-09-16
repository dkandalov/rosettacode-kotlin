package scripts.implementation

import org.http4k.core.HttpHandler
import java.io.File
import java.net.SocketTimeoutException

private val excludedTasks = listOf(
    // kotlin native
    "Address_of_a_variable",
    "Create_an_object_at_a_given_address",
    "Call_a_foreign-language_function",
    "Check_input_device_is_a_terminal",
    "Check_output_device_is_a_terminal",
    "Machine_code",
    "OpenGL",
    "Pointers_and_references",
    "Positional_read",
    "Terminal_control-Positional_read",
    "Window_creation",

    // kotlin js
    "Respond_to_an_unknown_method_call",
    "Runtime_evaluation",
    "Send_an_unknown_method_call",

    // ignored because there is no code
    "Boolean_values",
    "Interactive_programming",

    "matrix_NG,_Contined_Fraction_N", // because of https://youtrack.jetbrains.com/issue/KT-10494
    "Calendar_-_for_%22REAL%22_programmers" // because some code snippets for it are uppercase and not really compilable
)

fun pushLocalChangesToRosettaCode(httpClient: HttpHandler = newHttpClient()) {
    val snippetStorage = loadCodeSnippets(excludedTasks, httpClient)

    snippetStorage.snippetsWithDiffs.apply {
        if (isEmpty()) return log(">>> Nothing to push. There are no differences between code in local files and rosetta code website.")

        var cookies = cached("loginCookieJar") { loginAndGetCookies(httpClient) }
        if (cookies.list.isEmpty() || cookies.anyIsExpired()) {
            log("Login cookies have expired entries. Please login to RosettaCode website again.")
            cookies = cached("loginCookieJar", replace = true) { loginAndGetCookies(httpClient) }
        }
        if (cookies.list.isEmpty()) return

        forEach { (webSnippet, localSnippet) ->
            val result = webSnippet.submitCodeChange(httpClient, localSnippet.sourceCode, cookies)
            when (result) {
                is EditPage.SubmitResult.Success -> {
                    log("Pushed local changes to ${webSnippet.editPageUrl}")
                }
                is EditPage.SubmitResult.Failure -> {
                    log("Failed to push local changes to ${webSnippet.editPageUrl} (Reason: ${result.reason})")
                }
            }
        }
    }

    snippetStorage.onlyLocalSnippets.apply {
        if (isEmpty()) return
        log(">>> Pushing newly create tasks to RosettaCode is currently not supported.\n" +
            "Therefore, you might want to add the following files manually.")
        forEach { log(it.filePath) }
    }
}

private fun loginAndGetCookies(httpClient: HttpHandler): Cookies {
    val (userName, password) = showLoginDialog() ?: return Cookies()
    if (userName.isEmpty() || password.isEmpty()) {
        log("Please specify non-empty user name and password to be able to login into RosettaCode website.")
        return Cookies()
    }
    return LoginPage.getWith(httpClient).login(httpClient, userName, password)
}

fun pullFromRosettaCodeWebsite(overwriteLocalFiles: Boolean = false, httpClient: HttpHandler = newHttpClient()) {
    val snippetStorage = loadCodeSnippets(excludedTasks, httpClient)

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
            log(">>> There are some tasks which have different source code locally and on rosetta code website.\n")

            if (overwriteLocalFiles) {
                log(">>> Local files will updated with content from rosetta code website.\n")
                forEach { (webCodeSnippet, localCodeSnippet) ->
                    LocalCodeSnippet.create(webCodeSnippet)
                    log("Overwritten ${localCodeSnippet.filePath}")
                }
            } else {
                log(">>> Please make necessary changes to keep github repository and rosetta code website in sync.\n")
                forEach { (webCodeSnippet, localCodeSnippet) ->
                    log("Differences between ${localCodeSnippet.filePath} and\n ${webCodeSnippet.editPageUrl}\n")
                }
            }
        } else {
            log(">>> There are no differences between code in local files and rosetta code website.")
        }
    }
}


private fun loadCodeSnippets(exclusions: List<String>, httpClient: HttpHandler): CodeSnippetStorage {
    val kotlinPage = cached("kotlinPage") { LanguagePage.getWith(httpClient) }
    val editPageUrls = cached("editPageUrls") {
        kotlinPage.extractTaskPageUrls().mapParallelWithProgress { url, progress ->
            log("Getting edit page url from $url ($progress)")
            retry(SocketTimeoutException::class) {
                TaskPage.getWith(httpClient, url).extractKotlinEditPageUrl()
            }
        }
    }

    val editPages = cached("editPages") {
        editPageUrls.mapParallelWithProgress { url, progress ->
            log("Getting source code from $url ($progress)")
            retry(SocketTimeoutException::class) { EditPage.getWith(httpClient, url) }
        }
    }

    val webCodeSnippets = editPages
        .flatMap { it.extractCodeSnippets() }
        .filter { (editPageUrl) ->
            exclusions.none { editPageUrl.value.contains(it) }
        }

    val localCodeSnippets = File(examplesPath).listFiles()
        .filter { !it.isDirectory && it.extension == "kt" }
        .map { LocalCodeSnippet(it.path) }

    return CodeSnippetStorage(webCodeSnippets, localCodeSnippets)
}
