package `show_the_epoch`

// version 1.1.1

import java.util.Date
import java.util.TimeZone
import java.text.DateFormat

fun main( args: Array<String>) {
    val epoch = Date(0)
    val format = DateFormat.getDateTimeInstance()
    format.timeZone = TimeZone.getTimeZone("UTC")
    println(format.format(epoch))
}