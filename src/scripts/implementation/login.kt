package scripts.implementation

import khttp.structures.cookie.CookieJar
import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import javax.swing.*


fun main(args: Array<String>) {
    println(showLoginDialog())
}

data class Credentials(val userName: String, val password: String)

/**
 * Use swing UI for getting user name and password because there seems to be no easy way get stdin when running gradle task.
 */
fun showLoginDialog(): Credentials? {
    val result = CompletableFuture<Credentials?>()
    JFrame().apply {
        val jFrame = this
        add(JPanel().apply {
            val userName = JTextField()
            val password = JPasswordField()

            layout = GridLayout(3, 2)

            add(JLabel("User name:"))
            add(userName)
            add(JLabel("Password:"))
            add(password)

            add(JButton("Cancel").apply {
                addActionListener {
                    jFrame.dispose()
                    result.complete(null)
                }
            })
            add(JButton("Ok").apply {
                addActionListener {
                    jFrame.dispose()
                    result.complete(Credentials(userName.text, password.password.joinToString("")))
                }
            })
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                result.complete(null)
            }
        })
        title = "RosettaCode Login"
        pack()
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true
    }
    return result.get()
}

fun CookieJar.hasExpiredEntries(now: Instant = Instant.now()): Boolean {
    return entries
        .map { it.value.split("; ").find { it.startsWith("expires=") } }
        .filterNotNull()
        .any {
            val s = it.replace("expires=", "").replace("-", " ")
            val dateTime = OffsetDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME)
            // Exclude year 1970 because there are some cookies which have timestamp just after 1970
            dateTime.year > 1970 && dateTime.toInstant() <= now
        }
}