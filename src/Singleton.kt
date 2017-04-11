package `singleton`

// version 1.1.1

object Singleton {
    fun speak() = println("I am a singleton")
}

fun main(args: Array<String>) {
    Singleton.speak()
}