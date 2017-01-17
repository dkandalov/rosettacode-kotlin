package `date_format`

// version 1.0.6

import java.util.*

fun main(args: Array<String>) {
    val now = GregorianCalendar()
    println("%tF".format(now))
    println("%tA, %1\$tB %1\$te, %1\$tY".format(now))
}