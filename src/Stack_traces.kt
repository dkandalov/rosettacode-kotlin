package `stack_traces`

// version 1.1.1 (stacktrace.kt which compiles to StacktraceKt.class)

fun myFunc() {
    println(Throwable().stackTrace.joinToString("\n"))
}

fun main(args:Array<String>) {
    myFunc()
    println("\nContinuing ... ")
}