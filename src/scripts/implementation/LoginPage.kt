package scripts.implementation

import khttp.post
import khttp.structures.cookie.CookieJar
import java.net.URLEncoder

data class LoginPage(val html: String, val cookies: CookieJar) {
    fun login(userName: String, password: String): CookieJar {
        if (html.isLoggedIn()) return cookies
        if (html.contains("I'm not a robot")) throw FailedToLogin("Can't login right now because login page has 'I'm not a robot' capture.")

        val loginToken = Regex("<input type=\"hidden\" value=\"(.+?)\"").find(html)!!.groups[1]!!.value
        val response = post(
                url = "http://rosettacode.org/mw/index.php?title=Special:UserLogin&action=submitlogin&type=login&returnto=Rosetta+Code",
                data = mapOf(
                        "wpName" to userName,
                        "wpPassword" to password,
                        "wpLoginAttempt" to "Log in",
                        "wpLoginToken" to loginToken
                ),
                cookies = cookies + mapOf("rosettacodeUserName" to URLEncoder.encode(userName, "UTF-8"))
        )
        if (!response.text.isLoggedIn()) throw FailedToLogin("statusCode = ${response.statusCode}")
        return response.cookies
    }

    companion object {
        fun get(url: String = "http://rosettacode.org/wiki/Special:UserLogin") = khttp.get(url).run {
            LoginPage(text, cookies)
        }

        fun String.isLoggedIn() = !contains(">Log in<") && contains(">Log out<") // Checking for "Log in" link in the top right corner of the web page.

        class FailedToLogin(message: String) : RuntimeException(message)
    }
}
