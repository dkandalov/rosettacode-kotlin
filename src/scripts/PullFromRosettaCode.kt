package scripts

import scripts.implementation.clearLocalWebCache

fun main(args: Array<String>) {
    clearLocalWebCache(excluding = "loginCookieJar.xml")
    pullFromRosettaCodeWebsite()
}