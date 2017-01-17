package `last_friday_of_each_month`

// version 1.0.6

import java.util.*

fun main(args: Array<String>) {
    print("Enter a year : ")
    val year = readLine()!!.toInt()
    println("The last Fridays of each month in $year are as follows:")
    val calendar = GregorianCalendar(year, 0, 31)
    var daysInMonth: Int
    var offset: Int
    var lastFriday: Int     
    for (month in 1..12) {    
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) 
        offset = (calendar[Calendar.DAY_OF_WEEK] - Calendar.FRIDAY)
        if (offset < 0) offset += 7     
        lastFriday  = daysInMonth - offset
        println("$year-" + "%02d-".format(month) + "%02d".format(lastFriday))
        if (month < 12) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
    }
}