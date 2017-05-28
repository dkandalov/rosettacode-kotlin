package `literals_integer`

// version 1.0.6

fun main(args: Array<String>) {
    val d = 255                  // decimal integer literal
    val h = 0xff                 // hexadecimal integer literal
    val b = 0b11111111           // binary integer literal

    val ld = 255L                // decimal long integer literal (can't use l instead of L)
    val lh = 0xffL               // hexadecimal long integer literal (could use 0X rather than 0x)
    val lb = 0b11111111L         // binary long integer literal (could use 0B rather than 0b)

    val sd : Short = 127         // decimal integer literal automatically converted to Short
    val sh : Short = 0x7f        // hexadecimal integer literal automatically converted to Short
    val bd : Byte  = 0b01111111  // binary integer literal automatically converted to Byte

    println("$d $h $b $ld $lh $lb $sd $sh $bd")   
}