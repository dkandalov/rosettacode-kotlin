package scripts

fun main(args: Array<String>) {
    clearLocalWebCache(excluding = "loginCookieJar.xml")
    syncRepoWithRosettaCodeWebsite()
}