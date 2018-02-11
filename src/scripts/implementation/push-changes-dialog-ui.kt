package scripts.implementation

import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CompletableFuture
import javax.swing.*


fun main(args: Array<String>) {
    println(showPushChangesDialog())
}

data class DialogData(
    val userName: String,
    val password: String,
    val changeSummary: String,
    val isMinorEdit: Boolean
)

/**
 * Use swing UI for getting user name and password because there seems to be no easy way get stdin when running gradle task.
 */
fun showPushChangesDialog(): DialogData? {
    val result = CompletableFuture<DialogData?>()
    JFrame().apply {
        val jFrame = this
        add(JPanel().apply {
            val env = System.getenv()
            val userName = JTextField().apply { text = env["USERNAME"] }
            val password = JPasswordField().apply{ text = env["PASSWORD"] }
            val changeSummary = JTextField().apply { text = "/* {{header|Kotlin}} */ change description" }
            val isMinorEdit = JCheckBox().apply {
                text = "this is a minor edit"
                isSelected = true
            }

            layout = GridLayout(5, 2)

            add(JLabel("User name:"))
            add(userName)
            add(JLabel("Password:"))
            add(password)
            add(JLabel("Change summary:"))
            add(changeSummary)
            add(JLabel(""))
            add(isMinorEdit)

            add(JButton("Cancel").apply {
                addActionListener {
                    jFrame.dispose()
                    result.complete(null)
                }
            })
            add(JButton("Ok").apply {
                addActionListener {
                    jFrame.dispose()
                    result.complete(DialogData(
                        userName.text,
                        password.password.joinToString(""),
                        changeSummary.text,
                        isMinorEdit.isSelected
                    ))
                }
            })
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                result.complete(null)
            }
        })
        title = "Push changes to RosettaCode"
        pack()
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true
    }
    return result.get()
}
