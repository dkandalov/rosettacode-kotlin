package `user_input_graphical`

// version 1.1.0
// compiled with -nowarn flag to disable 'variable not used' warning

import javax.swing.JOptionPane

fun main(args: Array<String>) {
    val s = JOptionPane.showInputDialog("Enter a string")
    do {
        val number = JOptionPane.showInputDialog("Enter 75000").toInt()
    }
    while (number != 75000)
}