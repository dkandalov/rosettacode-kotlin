package scripts

import scripts.implementation.http.newRCClient
import scripts.implementation.http.withDebug
import scripts.implementation.pushLocalChangesToRosettaCode

fun main() {
    pushLocalChangesToRosettaCode(newRCClient().withDebug())
}