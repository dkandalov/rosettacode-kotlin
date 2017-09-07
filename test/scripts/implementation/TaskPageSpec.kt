package scripts.implementation

import io.kotlintest.specs.StringSpec

val kotlinSectionId = 77 // Note that this id might change over time.

class TaskPageSpec: StringSpec() {
    init {
        "extract edit page urls from task page" {
            val html = "array-concatenation-page.txt".readFileText()
            val url = TaskPage(html).extractKotlinEditPageUrl()
            url shouldBe EditPageUrl("http://rosettacode.org//mw/index.php?title=Array_concatenation&action=edit&section=$kotlinSectionId")
        }
    }
}