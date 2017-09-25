package scripts

import org.http4k.core.Request
import org.http4k.core.Uri
import scripts.implementation.clearLocalWebCache
import scripts.implementation.newRecordingHttpClient
import scripts.implementation.pullFromRosettaCodeWebsite

val dirty = System.getProperty("dirty", "false").toBoolean()
val overwriteLocalFiles = System.getProperty("overwriteLocalFiles", "false").toBoolean()

fun main(args: Array<String>) {
    if (!dirty) clearLocalWebCache("loginCookieJar.xml", "httpRecording.xml")
    val httpClient = newRecordingHttpClient(
        shouldStore = { !(it is Request && it.uri == Uri.of("http://rosettacode.org/wiki/Category:Kotlin")) }
    )
    pullFromRosettaCodeWebsite(overwriteLocalFiles, httpClient)
}