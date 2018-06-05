package scripts.implementation.pages

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import scripts.implementation.http.RCClient
import scripts.implementation.WebCodeSnippet
import scripts.implementation.formData
import scripts.implementation.pages.EditPage.SubmitResult.Failure
import scripts.implementation.pages.EditPage.SubmitResult.Success

data class EditPage(val url: EditPageUrl, val html: String) {
    fun extractCodeSnippets(): List<WebCodeSnippet> {
        val html = textAreaContent().unEscapeXml()
        return html.sourceCodeRanges()
            .map { html.substring(it) }
            .mapIndexed { index, code -> WebCodeSnippet.create(url, code, index) }
    }

    fun submitCodeChange(
        rcClient: RCClient,
        changeSummary: String,
        newCode: String,
        isMinorEdit: Boolean,
        index: Int
    ): SubmitResult {
        fun String.valueOfTag(tagName: String): String {
            val i1 = indexOf("name=\"$tagName\"")
            if (i1 == -1) return ""
            var i2 = lastIndexOf("value=\"", i1)
            if (i2 == -1) return "" else i2 += "value=\"".length
            val i3 = indexOf("\"", i2)
            if (i3 == -1) return ""
            return substring(i2, i3)
        }
        fun String.hasPermissionError() = this.contains("class=\"permissions-errors\"")

        if (html.hasPermissionError()) return Failure("permission error; try removing login cookie")

        val updatedContent = textAreaContent().unEscapeXml().run {
            val ranges = sourceCodeRanges()
            if (index <= ranges.size - 1) replaceRange(ranges[index], newCode)
            else this + newCode
        }.replaceKotlinTagWithScala().escapeXml()

        val section = html.valueOfTag("wpSection")
        val startTime = html.valueOfTag("wpStarttime")
        val editTime = html.valueOfTag("wpEdittime")
        val parentRevId = html.valueOfTag("parentRevId")
        val oldId = html.valueOfTag("oldid")
        val autoSummary = html.valueOfTag("wpAutoSummary")
        val editToken = html.valueOfTag("wpEditToken")

        val hasMissingFields = listOf(section, startTime, editTime, parentRevId, oldId, autoSummary, editTime).any { it.isEmpty() }
        if (hasMissingFields) return Failure("some of required fields are missing from edit page")

        val formParameters = listOf(
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
        ).let {
            if (isMinorEdit) it + Pair("wpMinoredit", "1") else it
        }

        // TODO some pages seem to only work with http instead of https url
        // E.g. http://rosettacode.org//mw/index.php?title=Sort_an_array_of_composite_structures&action=edit&section=40
        // and http://rosettacode.org//mw/index.php?title=Zeckendorf_number_representation&action=edit&section=29
        val request = Request(POST, "https://rosettacode.org/mw/index.php?title=${url.pageId()}&action=submit")
            .header("Host", "rosettacode.org")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Referer", LoginPage.url)
            .formData(formParameters)

        var response = rcClient(request)
        val redirectLocation = response.header("Location")
        if (!(response.status == Status.FOUND && redirectLocation != null)) {
            throw LoginPage.Companion.FailedToLogin("Expected redirect response but was ${response.status} with location ${response.header("Location")}")
        }
        rcClient.removeFromCache { it.contains(redirectLocation) }
        response = rcClient(Request(GET, redirectLocation))

        return when {
            response.status != OK -> Failure(response.status.toString())
            response.bodyString().hasPermissionError() -> Failure("permission error; try removing login cookie")
            else -> Success
        }
    }

    fun textAreaContent(): String {
        val i1 = html.findIndex(0, "id=\"wpTextbox1\"") ?: return ""
        val i2 = html.findIndex(i1, ">") ?: return ""
        val i3 = html.findIndex(i2, "</textarea>") ?: return ""
        return html.substring(i2 + 1, i3)
    }

    companion object {
        // List only specific tags because some examples contain <lang html> or another language by mistake.
        private val openingTags = listOf("<lang Kotlin>", "<lang kotlin>", "<lang scala>", "<lang java>", "<lang groovy>")
        private val closingTag = "</lang>"

        fun getWith(httpClient: HttpHandler, url: EditPageUrl): EditPage {
            val request = Request(GET, url.value)
            return EditPage(url, httpClient(request).bodyString())
        }

        private fun String.sourceCodeRanges(startFrom: Int = 0): List<IntRange> {
            val i1 = findIndex(startFrom, openingTags) ?: return emptyList()
            val i2 = findIndex(i1, ">") ?: return emptyList()
            val i3 = findIndex(i2, closingTag) ?: return emptyList()

            return listOf(IntRange(i2 + 1, i3 - 1)) + sourceCodeRanges(i3 + 1)
        }

        private fun String.findIndex(from: Int, s: String): Int? {
            val i = indexOf(s, from)
            return if (i == -1) null else i
        }

        private fun String.findIndex(from: Int, strings: List<String>): Int? {
            strings.forEach { s ->
                val i = indexOf(s, from)
                if (i != -1) return i
            }
            return null
        }

        private fun String.unEscapeXml() = replace("&lt;", "<").replace("&amp;", "&")
        private fun String.escapeXml() = this // Don't escape anything because media wiki doesn't like everything escaped and can handle escaping itself anyway.
        private fun String.replaceKotlinTagWithScala() =
            // Do this because RosettaCode highlights kotlin as scala better than kotlin as kotlin.
            replace("<lang kotlin>", "<lang scala>").replace("<lang Kotlin>", "<lang scala>")
    }

    sealed class SubmitResult {
        object Success: SubmitResult()
        data class Failure(val reason: String): SubmitResult()
    }
}