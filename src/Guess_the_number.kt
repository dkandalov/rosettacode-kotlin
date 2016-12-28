package `guess_the_number`

// version 1.0.5-2

fun main(args: Array<String>) {
    val rand = java.util.Random()
    val n = 1 + rand.nextInt(10)
    var guess: Int
    println("Guess which number I've chosen in the range 1 to 10\n")
    while (true) {
        print(" Your guess : ")
        guess = readLine()!!.toInt()
        if (n == guess) {
            println("\nWell guessed!")
            return
        }
    } 
}