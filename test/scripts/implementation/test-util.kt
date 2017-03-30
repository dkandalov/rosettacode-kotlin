package scripts.implementation

import khttp.get
import java.io.File

fun String.trimmed() = trimMargin().trim()

fun String.readText() = File("./test/scripts/implementation/$this").readText()

fun main(args: Array<String>) {
    fun downloadPagesForTestInput() {
        val basePath = "./test/scripts/"
        File("$basePath/kotlin-page.txt").writeText(get("http://rosettacode.org/wiki/Category:Kotlin").text)
        File("$basePath/array-concatenation-page.txt").writeText(get("http://rosettacode.org/wiki/Array_concatenation").text)
        File("$basePath/array-concatenation-edit-page.txt").writeText(get("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=70").text)
    }
    downloadPagesForTestInput()
}