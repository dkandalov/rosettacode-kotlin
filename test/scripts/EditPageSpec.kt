package scripts

import io.kotlintest.specs.StringSpec

class EditPageSpec : StringSpec() {
    init {
        "extract code snippets from task edit page" {
            val html = "array-concatenation-edit-page.txt".readText()
            val codeSnippets = EditPage(EditPageUrl(""), html).extractKotlinSource()
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
            ).map{ it.trimmed() }
        }

        "extract text area from task edit page" {
            val html = "array-concatenation-edit-page.txt".readText()
            val text = EditPage(EditPageUrl(""), html).textAreaContent().trimmed()
            text shouldBe """
                |=={{header|Kotlin}}==
                |There is no operator or standard library function for concatenating &lt;code>Array&lt;/code> types. One option is to convert to &lt;code>Collection&lt;/code>s, concatenate, and convert back:
                |&lt;lang kotlin>fun main(args: Array&lt;String>) {
                |    val a: Array&lt;Int> = arrayOf(1, 2, 3) // initialise a
                |    val b: Array&lt;Int> = arrayOf(4, 5, 6) // initialise b
                |    val c: Array&lt;Int> = (a.toList() + b.toList()).toTypedArray()
                |    println(c)
                |}&lt;/lang>
                |
                |Alternatively, we can write our own concatenation function:
                |&lt;lang kotlin>fun arrayConcat(a: Array&lt;Any>, b: Array&lt;Any>): Array&lt;Any> {
                |    return Array(a.size + b.size, { if (it in a.indices) a[it] else b[it - a.size] })
                |}&lt;/lang>
                |
                |When working directly with &lt;code>Collection&lt;/code>s, we can simply use the &lt;code>+&lt;/code> operator:
                |&lt;lang kotlin>fun main(args: Array&lt;String>) {
                |    val a: Collection&lt;Int> = listOf(1, 2, 3) // initialise a
                |    val b: Collection&lt;Int> = listOf(4, 5, 6) // initialise b
                |    val c: Collection&lt;Int> = a + b
                |    println(c)
                |}&lt;/lang>
                """.trimmed()
        }
    }
}