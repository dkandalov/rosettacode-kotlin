package scripts.implementation

data class EditPageUrl(val value: String) {

    fun pageId() = value.replace(Regex(".*title="), "").replace(Regex("&action.*"), "")

    override fun toString() = value

    companion object {
        /**
         * Replace some characters because they are not "friendly" to be used as file name.
         */
        fun String.asFileName() =
            replace("/", "-").replace("%27", "").replace("%2B", "-plus-")
            .let { if (it.matches(Regex("\\d+.*"))) "_$it" else it } // workaround for https://youtrack.jetbrains.com/issue/KT-10494

        /**
         * Lowercase because it is "kind of java convention".
         * Replace "-" because of https://youtrack.jetbrains.com/issue/KT-15288
         */
        fun String.asPackageName() = asFileName().toLowerCase().replace("-", "_")
    }
}