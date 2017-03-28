package `number_names`

// version 1.1.1

val names = mapOf(
    1 to "one",
    2 to "two",
    3 to "three",
    4 to "four",
    5 to "five",
    6 to "six",
    7 to "seven",
    8 to "eight",
    9 to "nine",
    10 to "ten",
    11 to "eleven",
    12 to "twelve",
    13 to "thirteen",
    14 to "fourteen",
    15 to "fifteen",
    16 to "sixteen",
    17 to "seventeen",
    18 to "eighteen",
    19 to "nineteen",
    20 to "twenty",
    30 to "thirty",
    40 to "forty",
    50 to "fifty",
    60 to "sixty",
    70 to "seventy",
    80 to "eighty",
    90 to "ninety"
)
val bigNames = mapOf(
    1_000L to "thousand",
    1_000_000L to "million",
    1_000_000_000L to "billion",
    1_000_000_000_000L to "trillion",
    1_000_000_000_000_000L to "quadrillion",
    1_000_000_000_000_000_000L to "quintillion"
)

fun numToText(n: Long, uk: Boolean = false): String {
    if (n == 0L) return "zero"
    val neg = n < 0L
    val maxNeg = n == Long.MIN_VALUE
    var nn = if (maxNeg) -(n + 1) else if (neg) -n else n
    val digits3 = IntArray(7)
    for (i in 0..6) {  // split number into groups of 3 digits from the right
        digits3[i] = (nn % 1000).toInt()
        nn /= 1000
    }

    fun threeDigitsToText(number: Int) : String {
        val sb = StringBuilder()
        if (number == 0) return ""
        val hundreds = number / 100
        val remainder = number % 100
        if (hundreds > 0) {
            sb.append(names[hundreds], " hundred")
            if (remainder > 0) sb.append(if (uk) " and " else " ")
        }
        if (remainder > 0) {
            val tens = remainder / 10
            val units = remainder % 10
            if (tens > 1) {
                sb.append(names[tens * 10])
                if (units > 0) sb.append("-", names[units])
            }
            else sb.append(names[remainder])
        }
        return sb.toString()
    }

    val strings = Array<String>(7) { threeDigitsToText(digits3[it]) }
    var text = strings[0]
    var andNeeded = uk && digits3[0] in 1..99
    var big = 1000L
    for (i in 1..6) {
        if (digits3[i] > 0) {
            var text2 = strings[i] + " " + bigNames[big]
            if (text.length > 0) {
                text2 += if (andNeeded) " and " else ", "
                andNeeded = false
            }
            else andNeeded = uk && digits3[i] in 1..99
            text = text2 + text
        }
        big *= 1000
    }
    if (maxNeg) text = text.dropLast(5) + "eight"
    return (if (neg) "minus " else "") + text
}

fun main(args: Array<String>) {
    val la = longArrayOf(
        0, 1, 7, 10, 18, 22, 67, 99, 100, 105, 999, -1056, 1000005000,
        2074000000, 1234000000745003L, Long.MIN_VALUE
    )
    println("Using US representation:")
    for (i in la) println("${"%20d".format(i)} = ${numToText(i)}")
    println()
    println("Using UK representation:")
    for (i in la) println("${"%20d".format(i)} = ${numToText(i, true)}")
}