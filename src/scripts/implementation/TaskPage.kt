package scripts.implementation

data class TaskPage(val html: String) {
    internal fun extractKotlinEditPageUrl(): EditPageUrl {
        val s = html.split("\n")
                .dropWhile { !it.contains("<span class=\"mw-headline\" id=\"Kotlin\">") }
                .find { it.contains("<a href=\"/mw/") }!!

        return EditPageUrl(s.extractUrl().replace("&amp;", "&"))
    }

    companion object {
        fun get(url: String): TaskPage = TaskPage(khttp.get(url).text)
    }
}