package scripts

import io.kotlintest.specs.StringSpec
import khttp.get
import java.io.File

class SyncWithRosettaCodeTest: StringSpec() {
    init {
        "extract task page urls from Kotlin page" {
            val html = "kotlin-page.txt".readText()
            val urls = LanguagePage(html).extractTaskPageUrls()

            urls should containInAnyOrder(
                    "http://rosettacode.org//wiki/100_doors",
                    "http://rosettacode.org//wiki/2048",
                    "http://rosettacode.org//wiki/24_game",
                    "http://rosettacode.org//wiki/9_billion_names_of_God_the_integer",
                    "http://rosettacode.org//wiki/99_Bottles_of_Beer"
            )
            urls.size shouldBe 117
        }

        "extract edit page urls from task page" {
            val html = "array-concatenation-page.txt".readText()
            val url = TaskPage(html).extractKotlinEditPageUrl()
            url shouldBe EditPageUrl("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=70")
        }

        "extract code snippets from task edit page" {
            val html = "array-concatenation-edit-page.txt".readText()
            val codeSnippets = EditPage(EditPageUrl.none, html).extractKotlinSource()
            codeSnippets shouldBe listOf(
                """
                |fun main(args: Array<String>) {
                |    val a: Array<Int> = arrayOf(1, 2, 3) // initialise a
                |    val b: Array<Int> = arrayOf(4, 5, 6) // initialise b
                |    val c: Array<Int> = (a.toList() + b.toList()).toTypedArray()
                |    println(c)
                |}
                """,
                """
                |fun arrayConcat(a: Array<Any>, b: Array<Any>): Array<Any> {
                |    return Array(a.size + b.size, { if (it in a.indices) a[it] else b[it - a.size] })
                |}
                """,
                """
                |fun main(args: Array<String>) {
                |    val a: Collection<Int> = listOf(1, 2, 3) // initialise a
                |    val b: Collection<Int> = listOf(4, 5, 6) // initialise b
                |    val c: Collection<Int> = a + b
                |    println(c)
                |}
                """
            ).map{ it.asCode() }
        }
    }

    private fun String.asCode() = trimMargin().trim()

    private fun String.readText() = File("./test/scripts/$this").readText()

    @Suppress("unused") // Temporarily add this method to test class "init" to update test data.
    private fun downloadPagesForTestInput() {
        val basePath = "./test/scripts/"
        File("$basePath/kotlin-page.txt").writeText(get("http://rosettacode.org/wiki/Category:Kotlin").text)
        File("$basePath/array-concatenation-page.txt").writeText(get("http://rosettacode.org/wiki/Array_concatenation").text)
        File("$basePath/array-concatenation-edit-page.txt").writeText(get("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=70").text)
    }
}