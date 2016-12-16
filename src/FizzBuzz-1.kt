package `fizzbuzz_1`

fun fizzBuzz1() {
    fun fizzbuzz(x: Int) = if(x % 15 == 0) "FizzBuzz" else x
    fun fizz(x: Any) = if(x is Int && x % 3  == 0) "Buzz" else x
    fun buzz(x: Any) = if(x is Int && x.toInt() % 5 == 0) "Fizz" else x

    (1..100).map { fizzbuzz(it) }.map { fizz(it) }.map { buzz(it) }.forEach { println(it) }
}