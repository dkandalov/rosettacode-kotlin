package `execute_a_system_command`

// version 1.0.6

import java.util.*

fun main(args: Array<String>) {
    val proc = Runtime.getRuntime().exec("cmd /C dir")  // testing on Windows 10
    val scan = Scanner(proc.inputStream)    		
    while (scan.hasNextLine()) println(scan.nextLine())
    scan.close()
}