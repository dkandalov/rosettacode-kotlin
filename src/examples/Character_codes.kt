package `character_codes`

fun main(args: Array<String>) {
    var c = 'a'
    var i = c.toInt()
    println("$c  <-> $i")
    i += 2
    c = i.toChar()
    println("$i <-> $c")
}