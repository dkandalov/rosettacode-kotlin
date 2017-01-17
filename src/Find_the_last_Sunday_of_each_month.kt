package `find_the_last_sunday_of_each_month`

// version 1.0.6

import java.util.*

fun main(args: Array<String>) {
    print("Enter a year : ")
    val year = readLine()!!.toInt()
    println("The last Sundays of each month in $year are as follows:")
    val calendar = GregorianCalendar(year, 0, 31)
    var daysInMonth: Int
    var lastSunday: Int 
    for (month in 1..12) {    
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)      
        lastSunday  = daysInMonth - (calendar[Calendar.DAY_OF_WEEK] - Calendar.SUNDAY)
        println("$year-" + "%02d-".format(month) + "%02d".format(lastSunday))
        if (month < 12) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
    }
}