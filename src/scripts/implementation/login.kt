package scripts.implementation

import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
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
