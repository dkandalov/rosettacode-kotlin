package scripts

import scripts.implementation.http.newRCClient
import scripts.implementation.pullFromRosettaCodeWebsite

fun main(args: Array<String>) {
    pullFromRosettaCodeWebsite(
        overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false")!!.toBoolean(),
        rcClient = newRCClient(),
        dirty = System.getProperty("dirty", "true")!!.toBoolean()
    )
}