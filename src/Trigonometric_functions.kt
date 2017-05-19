package `trigonometric_functions`

// version 1.1.2

import java.lang.Math.*

fun main(args: Array<String>) {
    val radians = Math.PI / 4.0
    val degrees = 45.0
    val conv = Math.PI / 180.0
    val f = "%1.15f"
    var inverse: Double

    println("                Radians              Degrees")
    println("Angle      : ${f.format(radians)}\t ${f.format(degrees)}\n")
    println("Sin        : ${f.format(sin(radians))}\t  ${f.format(sin(degrees * conv))}")
    println("Cos        : ${f.format(cos(radians))}\t  ${f.format(cos(degrees * conv))}")
    println("Tan        : ${f.format(tan(radians))}\t  ${f.format(tan(degrees * conv))}\n")
    inverse = asin(sin(radians))
    println("ASin(Sin)  : ${f.format(inverse)}\t ${f.format(inverse / conv)}")
    inverse = acos(cos(radians))
    println("ACos(Cos)  : ${f.format(inverse)}\t ${f.format(inverse / conv)}")
    inverse = atan(tan(radians))
    println("ATan(Tan)  : ${f.format(inverse)}\t ${f.format(inverse / conv)}")
}