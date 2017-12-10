package `https_authenticated`

// version 1.2.0

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object PasswordAuthenticator : Authenticator() {
    override fun getPasswordAuthentication() =
        PasswordAuthentication ("username", "password".toCharArray())
}

fun main(args: Array<String>) {
    val url = URL("https://somehost.com")
    val con = url.openConnection() as HttpsURLConnection
    Authenticator.setDefault(PasswordAuthenticator)
    con.allowUserInteraction = true
    con.connect()
    val isr = InputStreamReader(con.inputStream)
    val br = BufferedReader(isr)
    while (true) {
        val line = br.readLine()
        if (line == null) break
        println(line)
    }
}