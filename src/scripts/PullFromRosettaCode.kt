package scripts

import scripts.implementation.asRCClient
import scripts.implementation.newHttpClient
import scripts.implementation.pullFromRosettaCodeWebsite

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    pullFromRosettaCodeWebsite(overwriteLocalFiles, newHttpClient().asRCClient(), dirty)
}