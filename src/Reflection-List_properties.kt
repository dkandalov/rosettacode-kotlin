package `reflection_list_properties`

// version 1.0.6

import kotlin.reflect.*
import kotlin.reflect.jvm.*

open class BaseExample(val baseProp: String) {
    protected val protectedProp: String = "inherited protected value"
}

class Example(val prop1: String, val prop2: Int, baseProp: String) : BaseExample(baseProp) {  
    private val privateProp: String = "private value"
    
    val prop3: String
        get() = "property without backing field"

    val prop4 by lazy { "delegated value" }
}

fun main(args: Array<String>) {
    val example = Example(prop1 = "abc", prop2 = 1, baseProp = "inherited public value")
    val props = Example::class.memberProperties
    for (prop in props) {
        prop.isAccessible = true  // makes non-public properties accessible
        println("${prop.name.padEnd(13)} -> ${prop.get(example)}")
    }    
}