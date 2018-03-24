package scripts.implementation

import org.http4k.core.HttpHandler
import scripts.implementation.LocalCodeSnippet.Companion.tasksPath
import scripts.implementation.pages.EditPage
import scripts.implementation.pages.getKotlinLanguagePage
import scripts.implementation.pages.getLoginPage
import scripts.implementation.pages.getTaskPage
import java.io.File

private val excludedTasks = listOf(
    "Pattern_matching", // TODO locally needs different import instead of "import Color.*"

    // kotlin native
    "Address_of_a_variable",
    "Arena_storage_pool",
    "Create_an_object_at_a_given_address",
    "Call_a_function_in_a_shared_library",
    "Call_a_foreign-language_function",
    "Check_input_device_is_a_terminal",
    "Check_output_device_is_a_terminal",
    "Greed",
    "FTP",
    "Machine_code",
    "Musical_scale",
    "OpenGL",
    "Pointers_and_references",
    "Positional_read",
    "SOAP",
    "Terminal_control-Positional_read",
    "Terminal_control-Restricted_width_positional_input",
    "Snake",
    "Window_creation/X11",
    "Use_another_language_to_call_a_function",

    // kotlin js
    "JSON",
    "Respond_to_an_unknown_method_call",
    "Runtime_evaluation",
    "Send_an_unknown_method_call",
    "XML_Validation",

    // ignored because there is no code
    "Boolean_values",
    "Interactive_programming",

    "matrix_NG,_Contined_Fraction_N", // because of https://youtrack.jetbrains.com/issue/KT-10494
    "Calendar_-_for_%22REAL%22_programmers" // because some code snippets for it are uppercase and not really compilable
)

fun pushLocalChangesToRosettaCode(rcClient: RCClient = newHttpClient().asRCClient()) {
    val snippetStorage = loadCodeSnippets(rcClient)

    snippetStorage.snippetsWithDiffs.apply {
        if (isEmpty()) return log(">>> Nothing to push. There are no differences between code in local files and rosetta code website.")

        val (userName, password, changeSummary, isMinorEdit) = showPushChangesDialog() ?: return
        if (userName.isEmpty() || password.isEmpty()) {
            return log("Please specify non-empty user name and password to be able to login into RosettaCode website.")
        }
        val loggedIn = rcClient.getLoginPage().login(rcClient, userName, password)
        if (loggedIn) log("Logged in.") else return

        forEach { (webSnippet, localSnippet) ->
            val result = webSnippet.submitCodeChange(rcClient, changeSummary, localSnippet.sourceCode, isMinorEdit)
            when (result) {
                is EditPage.SubmitResult.Success -> {
                    log("Pushed local changes to ${webSnippet.editPageUrl}")
                    rcClient.removeFromCache { it.contains(webSnippet.title) }
                }
                is EditPage.SubmitResult.Failure -> {
                    rcClient.removeFromCache { it.contains(webSnippet.title) }
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

fun pullFromRosettaCodeWebsite(overwriteLocalFiles: Boolean, rcClient: RCClient, dirty: Boolean) {
    if (!dirty) {
        log(">>> Clearing local cache")
        rcClient.clearCache()
    }

    val snippetStorage = loadCodeSnippets(rcClient)

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
                    val tempFile = createTempFile("rosetta-code-website-")
                    tempFile.writeText(webCodeSnippet.sourceCode)
                    log("idea diff ${localCodeSnippet.filePath} ${tempFile.absolutePath}\n")
                }
            }
        } else {
            log(">>> There are no differences between code in local files and rosetta code website.")
        }
    }
}


private fun loadCodeSnippets(httpClient: HttpHandler): CodeSnippetStorage {
    val kotlinPage = httpClient.getKotlinLanguagePage()
    val editPageUrls = kotlinPage.extractTaskPageUrls().mapParallelWithProgress { url, progress ->
        log("Getting edit page url from $url ($progress)")
        httpClient.getTaskPage(url).extractKotlinEditPageUrl()
    }

    val editPages = editPageUrls.mapParallelWithProgress { url, progress ->
        log("Getting source code from $url ($progress)")
        EditPage.getWith(httpClient, url)
    }

    val webCodeSnippets = editPages
        .flatMap { it.extractCodeSnippets() }
        .filterNot { codeSnippet ->
            excludedTasks.any { task -> codeSnippet.editPageUrl.value.contains(task) }
        }

    val localCodeSnippets = File(tasksPath).listFiles()
        .filter { it.isFile && it.extension == "kt" }
        .map { LocalCodeSnippet(it.path) }

    return CodeSnippetStorage(webCodeSnippets, localCodeSnippets)
}
