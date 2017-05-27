package scripts

import scripts.implementation.clearLocalWebCache
import scripts.implementation.pullFromRosettaCodeWebsite

fun main(args: Array<String>) {
    clearLocalWebCache(excluding = "loginCookieJar.xml")
    val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()
    pullFromRosettaCodeWebsite(overwriteLocalFiles)
}