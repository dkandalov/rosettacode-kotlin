package `luhn_test_of_credit_card_numbers`

// version 1.0.6

fun luhn(s: String): Boolean {
    fun sumDigits(n : Int) =  n / 10 + n % 10        
    val  t = s.reversed()
    val s1 = t.filterIndexed { i, it -> i % 2 == 0 }.sumBy { it - '0' }
    val s2 = t.filterIndexed { i, it -> i % 2 == 1 }.map { sumDigits((it - '0') * 2) }.sum() 
    return (s1 + s2) % 10 == 0
}

fun main(args: Array<String>) {
    val numbers = arrayOf("49927398716", "49927398717", "1234567812345678", "1234567812345670")
    for (number in numbers) println("${number.padEnd(16)} is ${if(luhn(number)) "valid" else "invalid"}")
}