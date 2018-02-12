package scripts.implementation

import scripts.implementation.LocalCodeSnippet.Companion.postfixedWith
import scripts.implementation.pages.EditPage
import scripts.implementation.pages.EditPageUrl
import scripts.implementation.pages.EditPageUrl.Companion.asFileName
import scripts.implementation.pages.EditPageUrl.Companion.asPackageName
import java.io.File

const val tasksPath: String = "src/tasks"

data class CodeSnippetStorage(
    private val webSnippets: List<WebCodeSnippet>,
    private val localSnippets: List<LocalCodeSnippet>
) {
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
            .filter{ (web, local) ->
                web.sourceCode != local.sourceCode
            }
}

data class CodeSnippetId(val value: String)

data class LocalCodeSnippet(val filePath: String) {
    val id = CodeSnippetId(File(filePath).name.replace(".kt", ""))
    val sourceCode
        get() = File(filePath).readText().trimPackage().trimLineEnds()

    companion object {
        fun create(codeSnippet: WebCodeSnippet): LocalCodeSnippet = codeSnippet.run {
            val sourceCode = sourceCode.trim().trimLineEnds().let {
                if (it.startsWith("package ")) it
                else "package ${codeSnippet.snippetPackageName()}\n\n" + it
            }
            val localFile = File("$tasksPath/${localFileName(editPageUrl, index)}")
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
    }
}

data class WebCodeSnippet(val editPageUrl: EditPageUrl, val sourceCode: String, val index: Int) {
    val id = CodeSnippetId(editPageUrl.pageId().asFileName().postfixedWith(index))
    val title = editPageUrl.value.substringAfter("title=").substringBefore("&")

    fun submitCodeChange(rcClient: RCClient, changeSummary: String, newCode: String, isMinorEdit: Boolean): EditPage.SubmitResult {
        val editPage = EditPage.getWith(rcClient, editPageUrl)
        return editPage.submitCodeChange(rcClient, changeSummary, newCode, isMinorEdit, index)
    }

    override fun toString() = "$editPageUrl - $index"

    companion object {
        fun create(url: EditPageUrl, code: String, index: Int) = WebCodeSnippet(url, code.trim().trimLineEnds(), index)
    }
}

private fun String.trimPackage() = trim().replace(Regex("^package .*\n"), "").trim()

// Do this because RosettaCode can have tasks with trailing spaces which can show up different to local files,
// however, when submitting code without trailing spaces RosettaCode seems to ignore it thinking nothing has changed.
private fun String.trimLineEnds() = split("\n").map(String::trimEnd).joinToString("\n")

fun String.extractUrl(): String {
    val s = replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
    return "http://rosettacode.org/$s"
}
