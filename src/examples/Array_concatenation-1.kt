package `array_concatenation_1`

fun arrayConcat(a: Array<Any>, b: Array<Any>): Array<Any> {
    return Array(a.size + b.size, { if (it in a.indices) a[it] else b[it - a.size] })
}