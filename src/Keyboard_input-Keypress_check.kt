package `keyboard_input_keypress_check`

// version 1.0.6

import java.awt.event.*
import javax.swing.*

class Test() : JFrame() {
    init {
        println("Press any key to see its code or 'enter' to quit\n")
        addKeyListener(object : KeyAdapter() {
            override public fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                   setVisible(false)
                   dispose()
                   System.exit(0)
                } 
                else
                   println(e.keyCode)
            }
        })
    }
}

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val f = Test()
        f.setFocusable(true)
        f.setVisible(true)
    } 
}