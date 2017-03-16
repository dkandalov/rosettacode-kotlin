package scripts.implementation

import scripts.implementation.EditPageUrl.Companion.asFileName
import scripts.implementation.EditPageUrl.Companion.asPackageName
import scripts.implementation.LocalCodeSnippet.Companion.postfixedWith
import java.io.File

data class CodeSnippetId(val value: String)

data class LocalCodeSnippet(val filePath: String) {
    val id = CodeSnippetId(File(filePath).name.replace(".kt", ""))
    val sourceCode
        get() = java.io.File(filePath).readText().trimPackage()

    companion object {
        fun create(codeSnippet: WebCodeSnippet): LocalCodeSnippet = codeSnippet.run {
            val sourceCode = sourceCode.trim().trimLineEnds().let {
                if (it.startsWith("package ")) it
                else "package ${codeSnippet.snippetPackageName()}\n\n" + it
            }
            val localFile = java.io.File("src/${localFileName(editPageUrl, index)}")
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

    fun submitCodeChange(newCode: String, cookieJar: khttp.structures.cookie.CookieJar): khttp.responses.Response {
        val editPage = EditPage.get(editPageUrl, cookieJar)
        return editPage.submitCodeChange(newCode, index, cookieJar)
    }

    override fun toString(): String {
        return "$editPageUrl - $index"
    }

    companion object {
        fun create(url: EditPageUrl, code: String, index: Int) =
                WebCodeSnippet(url, code.trimPackage().trimLineEnds(), index)
    }
}

private fun String.trimPackage() = trim().replace(Regex("^package .*\n"), "").trim()

// Do this because RosettaCode can have examples with trailing spaces which can show up different to local files,
// however, when submitting code without trailing spaces RosettaCode seems to ignore it thinking nothing has changed.
private fun String.trimLineEnds() = split("\n").map(String::trimEnd).joinToString("\n")

fun String.extractUrl(): String {
    val s = replaceFirst(Regex(".*href=\""), "").replaceFirst(Regex("\".*"), "")
    return "http://rosettacode.org/$s"
}