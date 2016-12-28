package `guess_the_number_with_feedback`

// version 1.0.5-2

fun main(args: Array<String>) {
    val rand = java.util.Random()
    val n = 1 + rand.nextInt(20)
    var guess :Int
    println("Guess which number I've chosen in the range 1 to 20\n")
    while (true) {
        print(" Your guess : ")
        guess = readLine()!!.toInt()
        when (guess) {
            n              ->  { println("Correct, well guessed!") ; return }   
            in n + 1 .. 20 ->    println("Your guess is higher than the chosen number, try again")
            in 1 .. n - 1  ->    println("Your guess is lower than the chosen number, try again")
            else           ->    println("Your guess is inappropriate, try again")
        }                 
    }    
}