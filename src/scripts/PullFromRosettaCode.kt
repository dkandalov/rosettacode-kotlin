package scripts

import scripts.implementation.http.newRCClient
import scripts.implementation.pullFromRosettaCodeWebsite

fun main() {
    pullFromRosettaCodeWebsite(
        rcClient = newRCClient(),
        overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false")!!.toBoolean(),
        useCachedHttpClient = System.getProperty("dirty", "true")!!.toBoolean()
    )
}