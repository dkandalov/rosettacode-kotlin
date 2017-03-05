package `user_input_text`

// version 1.1.0
// compiled with -nowarn flag to disable 'variable not used' warning

fun main(args: Array<String>) {
    print("Enter a string : ")
    val s = readLine()!!
    do {
        print("Enter 75000 : ")
        val number = readLine()!!.toInt()
    }
    while (number != 75000) 
}