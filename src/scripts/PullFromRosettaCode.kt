package scripts

import scripts.implementation.http.newRCClient
import scripts.implementation.pullFromRosettaCodeWebsite

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    pullFromRosettaCodeWebsite(overwriteLocalFiles, newRCClient(), dirty)
}