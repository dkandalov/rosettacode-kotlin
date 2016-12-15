package scripts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import io.kotlintest.specs.StringSpec
import java.io.File

class DownloadTest: StringSpec() {
    init {
        "extract edit page urls from Kotlin lang page" {
            val xStream = XStream(XppDriver())
            val html = xStream.fromXML(File("./test/scripts/kotlinPage.xml")) as String

            val urls = LanguagePage(html).extractProblemPageUrls()

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