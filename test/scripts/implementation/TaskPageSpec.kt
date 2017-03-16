package scripts.implementation

import io.kotlintest.specs.StringSpec

class TaskPageSpec : StringSpec() {
    init {
        "extract edit page urls from task page" {
            val html = "array-concatenation-page.txt".readText()
            val url = TaskPage(html).extractKotlinEditPageUrl()
            url shouldBe EditPageUrl("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=70")
        }
    }
}