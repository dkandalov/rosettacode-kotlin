package scripts.implementation

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookies
import java.net.URLEncoder

data class LoginPage(val html: String, private val cookies: Cookies) {
    fun login(httpClient: HttpHandler, userName: String, password: String): Cookies {
        if (html.isLoggedIn()) return cookies
        if (html.contains("I'm not a robot")) throw FailedToLogin("Can't login right now because login page has 'I'm not a robot' capture.")

        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        val request = Request(POST, "https://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code")
            .with(cookies + Cookie("rosettacodeUserName", URLEncoder.encode(userName, "UTF-8")))
            .formData(listOf(
                "wpName" to userName,
                "wpPassword" to password,
                "wpLoginAttempt" to "Log in",
                "wpLoginToken" to loginToken
            ))

        val response = httpClient(request)

        if (!response.bodyString().isLoggedIn()) throw FailedToLogin("status = ${response.status}")
        return Cookies(response.cookies())
    }

    companion object {
        fun getWith(httpClient: HttpHandler, url: String = "http://rosettacode.org/wiki/Special:UserLogin") =
            httpClient(Request(GET, url)).run {
                LoginPage(bodyString(), Cookies(cookies()))
            }

        fun String.isLoggedIn() = !contains(">Log in<") && contains(">Log out<") // Checking for "Log in" link in the top right corner of the web page.

        class FailedToLogin(message: String): RuntimeException(message)
    }
}
