package `first_class_functions`

// version 1.0.6

fun compose(f: (Double) -> Double,  g: (Double) -> Double ): (Double) -> Double  = { f(g(it)) }

fun cube(d: Double) = d * d * d

fun main(args: Array<String>) {
    val listA = listOf(Math::sin, Math::cos, ::cube)
    val listB = listOf(Math::asin, Math::acos, Math::cbrt)
    val x = 0.5
    for (i in 0..2) println(compose(listA[i], listB[i])(x))
}