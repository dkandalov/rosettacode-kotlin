package scripts.implementation

import khttp.structures.cookie.CookieJar
import scripts.implementation.EditPage.SubmitResult.Failure
import scripts.implementation.EditPage.SubmitResult.Success

data class EditPage(val url: EditPageUrl, val html: String) {
    fun extractCodeSnippets(): List<WebCodeSnippet> {
        val html = textAreaContent().unEscapeXml()
        return html.sourceCodeRanges()
            .map{ html.substring(it) }
            .mapIndexed { index, code -> WebCodeSnippet.create(url, code, index) }
    }

    fun submitCodeChange(newCode: String, index: Int, cookieJar: CookieJar): SubmitResult {
        fun String.valueOfTag(tagName: String): String {
            val i1 = indexOf("name=\"$tagName\"")
            if (i1 == -1) return ""
            var i2 = lastIndexOf("value=\"", i1)
            if (i2 == -1) return "" else i2 += "value=\"".length
            val i3 = indexOf("\"", i2)
            if (i3 == -1) return ""
            return substring(i2, i3)
        }

        val updatedContent = textAreaContent().unEscapeXml().run {
            val ranges = sourceCodeRanges()
            if (index <= ranges.size - 1) replaceRange(ranges[index], newCode)
            else this + newCode
        }.replaceKotlinTagWithScala().escapeXml()

        val section = html.valueOfTag("wpSection")
        val startTime = html.valueOfTag("wpStarttime")
        val editTime = html.valueOfTag("wpEdittime")
        val changeSummary = "/* {{header|Kotlin}} */ Updated example see https://github.com/dkandalov/rosettacode-kotlin for details"
        val parentRevId = html.valueOfTag("parentRevId")
        val oldId = html.valueOfTag("oldid")
        val autoSummary = html.valueOfTag("wpAutoSummary")
        val editToken = html.valueOfTag("wpEditToken")

        val hasMissingFields = listOf(section, startTime, editTime, parentRevId, oldId, autoSummary, editTime).any { it.isNullOrEmpty() }
        if (hasMissingFields) return Failure("some of required fields are missing from edit page")

        val response = khttp.post(
            url = "https://rosettacode.org/mw/index.php?title=${url.pageId()}&action=submit",
            cookies = cookieJar,
            data = mapOf(
                "wpAntispam" to "",
                "wpSection" to section,
                "wpStarttime" to startTime,
                "wpEdittime" to editTime,
                "wpAutoSummary" to autoSummary,
                "oldId" to oldId,
                "parentRevId" to parentRevId,
                "format" to "text/x-wiki",
                "model" to "wikitext",
                "wpTextbox1" to updatedContent,
                "wpSummary" to changeSummary,
                "wpSave" to "Save page",
                "wpEditToken" to editToken,
                "wpUltimateParam" to "1"
            )
        )
        return if (response.statusCode != 200) Failure(response.statusCode.toString())
        else if (String(response.content).contains("permissions-errors")) Failure("permission error")
        else Success
    }

    fun textAreaContent(): String {
        val i1 = html.find(0, "id=\"wpTextbox1\"") ?: return ""
        val i2 = html.find(i1, ">") ?: return ""
        val i3 = html.find(i2, "</textarea>") ?: return ""
        return html.substring(i2 + 1, i3)
    }

    companion object {
        private val openingTags = listOf("<lang Kotlin>", "<lang kotlin>", "<lang scala>")
        private val closingTag = "</lang>"

        fun get(url: EditPageUrl, cookieJar: CookieJar = CookieJar()) = EditPage(url, khttp.get(url.value, cookies = cookieJar).text)

        private fun String.sourceCodeRanges(startFrom: Int = 0): List<IntRange> {
            val i1 = find(startFrom, 20) { s -> openingTags.any{ tag -> s.startsWith(tag) }} ?: return emptyList()
            val i2 = find(i1, ">") ?: return emptyList()
            val i3 = find(i2, closingTag) ?: return emptyList()

            return listOf(IntRange(i2 + 1, i3 - 1)) + sourceCodeRanges(i3 + 1)
        }

        private fun String.find(from: Int, s: String): Int? {
            val i = indexOf(s, from)
            return if (i == -1) null else i
        }

        private fun String.find(from: Int, viewSize: Int, predicate: (String) -> Boolean): Int? {
            return from.until(length).find { i ->
                val endIndex = i + viewSize
                predicate(substring(i, Math.min(endIndex, length)))
            }
        }

        private fun String.unEscapeXml() = replace("&lt;", "<").replace("&amp;", "&")
        private fun String.escapeXml() = this // Don't escape anything because media wiki doesn't like everything escaped and can handle escaping itself anyway.
        private fun String.replaceKotlinTagWithScala() =
                // Do this because RosettaCode highlights kotlin as scala better than kotlin as kotlin.
                replace("<lang kotlin>", "<lang scala>").replace("<lang Kotlin>", "<lang scala>")
    }

    sealed class SubmitResult {
        object Success : SubmitResult()
        data class Failure(val reason: String) : SubmitResult()
    }
}