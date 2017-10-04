package scripts

import scripts.implementation.asRCClient
import scripts.implementation.clearLocalWebCache
import scripts.implementation.newHttpClient
import scripts.implementation.pullFromRosettaCodeWebsite

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    if (!dirty) clearLocalWebCache("loginCookieJar.xml", "httpRecording.xml")
    val httpClient = newHttpClient().asRCClient()
    pullFromRosettaCodeWebsite(overwriteLocalFiles, httpClient)
}