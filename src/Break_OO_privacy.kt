package `break_oo_privacy`

// version 1.0.6

import kotlin.reflect.*
import kotlin.reflect.jvm.*

class ToBeBroken {
    private val secret: Int = 42
}

fun main(args: Array<String>) {
    val tbb = ToBeBroken()
    val props = ToBeBroken::class.declaredMemberProperties
    for (prop in props) {
        prop.isAccessible = true  // make private properties accessible
        println("${prop.name} -> ${prop.get(tbb)}")
    }    
}