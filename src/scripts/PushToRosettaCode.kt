package scripts

import scripts.implementation.asRCClient
import scripts.implementation.newHttpClient
import scripts.implementation.pushLocalChangesToRosettaCode

fun main(args: Array<String>) {
    pushLocalChangesToRosettaCode(newHttpClient().asRCClient())
}