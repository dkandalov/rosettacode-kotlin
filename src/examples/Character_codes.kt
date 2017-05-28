package `character_codes`

// version 1.0.5-2

fun main(args: Array<String>) {
    var c: Char = 'a'
    var i: Int  = c.toInt() 
    println("$c  <-> $i") 
    i += 2
    c = i.toChar()
    println("$i <-> $c")
}