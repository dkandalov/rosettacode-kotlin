package `function_composition`

// version 1.0.6

fun f(x: Int): Int = x * x

fun g(x: Int): Int = x + 2

fun compose(f: (Int) -> Int,  g: (Int) -> Int): (Int) -> Int  = { f(g(it)) }

fun main(args: Array<String>) {
   val x  = 10
   println(compose(::f, ::g)(x))
}