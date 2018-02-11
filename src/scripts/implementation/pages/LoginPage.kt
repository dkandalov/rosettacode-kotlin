package scripts.implementation.pages

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.cookie.Cookie
import org.http4k.filter.cookie.LocalCookie
import scripts.implementation.RCClient
import scripts.implementation.formData
import java.net.URLEncoder
import java.time.LocalDateTime

data class LoginPage(val html: String) {

    fun login(rcClient: RCClient, userName: String, password: String): Boolean {
        if (html.isLoggedIn()) return true
        if (html.contains("I'm not a robot")) throw FailedToLogin("Can't login right now because login page has 'I'm not a robot' capture.")

        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        rcClient.cookieStorage.store(listOf(LocalCookie(
            Cookie("rosettacodeUserName", URLEncoder.encode(userName, "UTF-8")), LocalDateTime.now()
        )))

        val request = Request(POST, "https://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code")
            .header("Host", "rosettacode.org")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Referer", "https://rosettacode.org/mw/index.php?title=Special:UserLogin")
            .formData(listOf(
                "wpName" to userName,
                "wpPassword" to password,
                "wpLoginAttempt" to "Log in",
                "wpLoginToken" to loginToken
            ))

        var response = rcClient(request)
        if (!(response.status == FOUND && response.header("Location") == "https://rosettacode.org/wiki/Rosetta_Code")) {
            throw FailedToLogin("Expected redirect response but was ${response.status} with location ${response.header("Location")}")
        }
        response = rcClient(Request(GET, response.header("Location")!!))
        if (!response.bodyString().isLoggedIn()) throw FailedToLogin("status = ${response.status}")
        return true
    }

    companion object {
        fun getWith(httpClient: HttpHandler, url: String = "https://rosettacode.org/wiki/Special:UserLogin") =
            httpClient(Request(GET, url)).run {
                LoginPage(bodyString())
            }

        fun String.isLoggedIn() = !contains(">Log in<") && contains(">Log out<") // Checking for "Log in" link in the top right corner of the web page.

        class FailedToLogin(message: String): RuntimeException(message)
    }
}
