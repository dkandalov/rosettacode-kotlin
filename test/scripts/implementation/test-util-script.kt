package scripts.implementation

import org.http4k.core.Method.GET
import org.http4k.core.Request
import java.io.File

fun String.trimmed() = trimMargin().trim()

fun String.readFileText() = File("./test/scripts/implementation/$this").readText()

fun main(args: Array<String>) {
    fun String.getWebPageText() = newHttpClient()(Request(GET, this)).bodyString()

    fun downloadPagesForTestInput() {
        val basePath = "./test/scripts/implementation/"

        File("$basePath/kotlin-page.txt")
            .writeText("http://rosettacode.org/wiki/Category:Kotlin".getWebPageText())

        File("$basePath/array-concatenation-page.txt")
            .writeText("http://rosettacode.org/wiki/Array_concatenation".getWebPageText())

        File("$basePath/array-concatenation-edit-page.txt")
            .writeText("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=$kotlinSectionId".getWebPageText())
    }
    downloadPagesForTestInput()
}