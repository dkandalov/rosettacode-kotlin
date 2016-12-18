package `count_in_octal`

//  version 1.0.5-2

//  counts up to 177 octal i.e. 127 decimal
fun main(args: Array<String>) {
    (0..Byte.MAX_VALUE).forEach { println("%03o".format(it)) }
}