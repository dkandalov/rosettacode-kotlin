package scripts.implementation

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.cookie.Cookie
import org.http4k.filter.cookie.LocalCookie
import java.net.URLEncoder
import java.time.LocalDateTime

data class LoginPage(val html: String) {

    fun login(rcClient: RCClient, userName: String, password: String): Boolean {
        if (html.isLoggedIn()) return true
        if (html.contains("I'm not a robot")) throw FailedToLogin("Can't login right now because login page has 'I'm not a robot' capture.")

        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        rcClient.cookieStorage.store(listOf(LocalCookie(Cookie("rosettacodeUserName", URLEncoder.encode(userName, "UTF-8")), LocalDateTime.now())))

        val request = Request(POST, "http://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code")
            .formData(listOf(
                "wpName" to userName,
                "wpPassword" to password,
                "wpLoginAttempt" to "Log in",
                "wpLoginToken" to loginToken
            ))

        val response = rcClient(request)

        if (!response.bodyString().isLoggedIn()) throw FailedToLogin("status = ${response.status}")
        return true
    }

    companion object {
        fun getWith(httpClient: HttpHandler, url: String = "http://rosettacode.org/wiki/Special:UserLogin") =
            httpClient(Request(GET, url)).run {
                LoginPage(bodyString())
            }

        fun String.isLoggedIn() = !contains(">Log in<") && contains(">Log out<") // Checking for "Log in" link in the top right corner of the web page.

        class FailedToLogin(message: String): RuntimeException(message)
    }
}
