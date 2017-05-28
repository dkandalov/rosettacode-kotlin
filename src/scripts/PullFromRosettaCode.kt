package scripts

import scripts.implementation.clearLocalWebCache
import scripts.implementation.pullFromRosettaCodeWebsite

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    if (!dirty) clearLocalWebCache(excluding = "loginCookieJar.xml")
    pullFromRosettaCodeWebsite(overwriteLocalFiles)
}