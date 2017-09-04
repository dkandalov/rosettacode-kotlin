package `call_an_object_method`

class MyClass {
    fun instanceMethod(s: String) = println(s)

    companion object {
        fun staticMethod(s: String) = println(s)
    }
}

fun main(args: Array<String>) {
    val mc = MyClass()
    mc.instanceMethod("Hello instance world!")
    MyClass.staticMethod("Hello static world!")
}