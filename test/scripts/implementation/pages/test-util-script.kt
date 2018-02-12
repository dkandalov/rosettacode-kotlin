package scripts.implementation.pages

import org.http4k.core.Method.GET
import org.http4k.core.Request
import scripts.implementation.newHttpClient
import java.io.File

fun String.trimmed() = trimMargin().trim()

fun String.readFileText() = File("./test/scripts/implementation/pages/$this").readText()

fun main(args: Array<String>) {
    fun String.getWebPageText() = newHttpClient()(Request(GET, this)).bodyString()

    fun downloadPagesForTestInput() {
        val basePath = "./test/scripts/implementation/pages/"

        File("$basePath/kotlin-page.txt").writeText(LanguagePage.url.getWebPageText())

        File("$basePath/array-concatenation-page.txt")
            .writeText("http://rosettacode.org/wiki/Array_concatenation".getWebPageText())

        File("$basePath/array-concatenation-edit-page.txt")
            .writeText("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=${kotlinSectionId}".getWebPageText())
    }
    downloadPagesForTestInput()
}