package scripts.implementation

import io.kotlintest.specs.StringSpec
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
    }
}