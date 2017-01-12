package `variadic_function`

// version 1.0.6

fun variadic(vararg va: String) {
    for (v in va) println(v)
}

fun main(args: Array<String>) {
    variadic("First", "Second", "Third")
    println("\nEnter four strings for the function to print:")
    val va = Array<String>(4) { "" }
    for (i in 1 .. 4) {
        print("String $i = ")
        va[i - 1] = readLine()!!
    }
    println()
    variadic(*va)   
}