package scripts.implementation

data class CodeSnippetStorage(val webSnippets: List<WebCodeSnippet>, val localSnippets: List<LocalCodeSnippet>) {
    val onlyLocalSnippets: List<LocalCodeSnippet>
        get() = localSnippets.filter { local -> webSnippets.none{ local.id == it.id } }

    val onlyWebSnippets: List<WebCodeSnippet>
        get() = webSnippets.filter{ web -> localSnippets.none{ web.id == it.id } }

    val snippetsWithDiffs: List<Pair<WebCodeSnippet, LocalCodeSnippet>>
        get() = webSnippets
            .map{ web ->
                val local = localSnippets.find { web.id == it.id }
                if (local != null) Pair(web, local) else null
            }
            .filterNotNull()
            .filter{ (web, local) -> web.sourceCode != local.sourceCode }
}