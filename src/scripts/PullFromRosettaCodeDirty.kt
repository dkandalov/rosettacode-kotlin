package scripts

import scripts.implementation.pullFromRosettaCodeWebsite

fun main(args: Array<String>) {
    val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()
    pullFromRosettaCodeWebsite(overwriteLocalFiles)
}