package scripts.implementation

import io.kotlintest.specs.StringSpec

class LanguagesPageSpec : StringSpec() {
    init {
        "extract task page urls from Kotlin language page" {
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
    }
}