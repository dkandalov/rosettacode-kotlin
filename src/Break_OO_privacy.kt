package `break_oo_privacy`

// version 1.0.6

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class ToBeBroken {
    @Suppress("unused")
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