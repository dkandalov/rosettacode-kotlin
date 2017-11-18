package scripts

import scripts.implementation.asRCClient
import scripts.implementation.newHttpClient
import scripts.implementation.pushLocalChangesToRosettaCode
import scripts.implementation.withDebug

fun main(args: Array<String>) {
    pushLocalChangesToRosettaCode(newHttpClient().withDebug().asRCClient())
}