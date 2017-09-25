package scripts.implementation

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import scripts.implementation.EditPageUrl.Companion.asFileName
import scripts.implementation.EditPageUrl.Companion.asPackageName

class EditPageUrlSpec: StringSpec() {
    init {
        "extract page edit id from url" {
            EditPageUrl("http://rosettacode.org/mw/index.php?title=Array_concatenation&action=edit&section=70").apply {
                pageId() shouldBe "Array_concatenation"
                pageId().asFileName() shouldBe "Array_concatenation"
                pageId().asPackageName() shouldBe "array_concatenation"
            }
            EditPageUrl("http://rosettacode.org/mw/index.php?title=A%2BB&action=submit").apply {
                pageId() shouldBe "A%2BB"
                pageId().asFileName() shouldBe "A-plus-B"
                pageId().asPackageName() shouldBe "a_plus_b"
            }
        }
    }
}