package scripts

import scripts.implementation.http.asRCClient
import scripts.implementation.http.newHttpClient
import scripts.implementation.pushLocalChangesToRosettaCode
import scripts.implementation.http.withDebug

fun main(args: Array<String>) {
    pushLocalChangesToRosettaCode(newHttpClient().withDebug().asRCClient())
}