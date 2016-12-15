package scripts

import io.kotlintest.specs.StringSpec
import java.io.File

class DownloadTest: StringSpec() {
    init {
        "extract task  page urls from Kotlin page" {
            val html = File("./test/scripts/kotlin_page.txt").readText()
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
            val html = File("./test/scripts/24_game_page.txt").readText()
            val url = TaskPage(html).extractKotlinEditUrl()
            url shouldBe "http://rosettacode.org//mw/index.php?title=24_game&action=edit&section=39"
        }
    }
}