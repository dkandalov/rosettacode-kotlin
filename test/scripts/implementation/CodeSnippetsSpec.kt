package scripts.implementation

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import scripts.implementation.pages.EditPageUrl
import java.io.File.createTempFile

class CodeSnippetsSpec: StringSpec() {
    init {
        "code is trimmed for both local and web snippets" {
            val code = "\n" +
                "package abc\n" +
                "  class A {}  \n" +
                "\n"
            val file = createTempFile("code-snippet", "")
            file.writeText(code)

            val localCodeSnippet = LocalCodeSnippet(file.absolutePath)
            localCodeSnippet.sourceCode shouldEqual "class A {}"

            val webCodeSnippet = WebCodeSnippet.create(EditPageUrl(""), code, 0)
            webCodeSnippet.sourceCode shouldEqual "" +
                "package abc\n" +
                "  class A {}"
        }

        "foo" {
            val snippet = WebCodeSnippet.create(
                url = EditPageUrl("http://rosettacode.org//mw/index.php?title=Stable_marriage_problem&action=edit&section=25"),
                code = "",
                index = 0
            )
            println(snippet.title.contains("marr"))
//            Getting source code from http://rosettacode.org//mw/index.php?title=Stable_marriage_problem&action=edit&section=25 (843 of 1008)

        }
    }
}