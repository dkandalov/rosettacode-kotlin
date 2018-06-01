package scripts.implementation.pages

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import scripts.implementation.pages.EditPageUrl.Companion.asFileName
import scripts.implementation.pages.EditPageUrl.Companion.asPackageName

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

            EditPageUrl("http://rosettacode.org/mw/index.php?title=Bitmap/B%C3%A9zier_curves/Cubic&action=edit&section=15")
                .pageId().asFileName() shouldBe "Bitmap-Bezier_curves-Cubic"

            EditPageUrl("http://rosettacode.org/mw/index.php?title=Vigen%C3%A8re_cipher&action=edit&section=27")
                .pageId().asFileName() shouldBe "Vigenere_cipher"
        }
    }
}