package scripts

import scripts.implementation.*

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    if (!dirty) {
        log(">>> Deleting files from local cache")
        clearLocalWebCache(exclusions = listOf("loginCookieJar.xml"), onFailedDelete = {
            log("Failed to delete ${it.name}")
        })
    }
    val httpClient = newHttpClient().asRCClient()
    pullFromRosettaCodeWebsite(overwriteLocalFiles, httpClient)
}