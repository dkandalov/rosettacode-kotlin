package `abbreviations,_simple`

// version 1.1.4-3

val r = Regex("[ ]+")

val table =
    "add 1  alter 3  backup 2  bottom 1  Cappend 2  change 1  Schange  Cinsert 2  Clast 3 " +
    "compress 4 copy 2 count 3 Coverlay 3 cursor 3  delete 3 Cdelete 2  down 1  duplicate " +
    "3 xEdit 1 expand 3 extract 3  find 1 Nfind 2 Nfindup 6 NfUP 3 Cfind 2 findUP 3 fUP 2 " +
    "forward 2  get  help 1 hexType 4  input 1 powerInput 3  join 1 split 2 spltJOIN load " +
    "locate 1 Clocate 2 lowerCase 3 upperCase 3 Lprefix 2  macro  merge 2 modify 3 move 2 " +
    "msg  next 1 overlay 1 parse preserve 4 purge 3 put putD query 1 quit  read recover 3 " +
    "refresh renum 3 repeat 3 replace 1 Creplace 2 reset 3 restore 4 rgtLEFT right 2 left " +
    "2  save  set  shift 2  si  sort  sos  stack 3 status 4 top  transfer 3  type 1  up 1 "

fun validate(commands: List<String>, minLens: List<Int>, words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList<String>()
    val results = mutableListOf<String>()
    for (word in words) {
        var matchFound = false
        for ((i, command) in commands.withIndex()) {
            if (minLens[i] == 0 || word.length !in minLens[i] .. command.length) continue
            if (command.startsWith(word, true)) {
                results.add(command.toUpperCase())
                matchFound = true
                break
            }
        }
        if (!matchFound) results.add("*error*")
    }
    return results
}

fun main(args: Array<String>) {
    val splits = table.trimEnd().split(r)
    val commands = mutableListOf<String>()
    val minLens = mutableListOf<Int>()
    var i = 0
    while (true) {
        commands.add(splits[i])
        val len = splits[i].length
        if (i == splits.size - 1) {
            minLens.add(len)
            break
        }
        val num = splits[++i].toIntOrNull()
        if (num != null) {
            minLens.add(minOf(num, len))
            if (++i == splits.size) break
        }
        else minLens.add(len)
    }
    val sentence = "riG   rePEAT copies  put mo   rest    types   fup.    6       poweRin"
    val words = sentence.trim().split(r)
    val results = validate(commands, minLens, words)
    print("user words:  ")
    for (j in 0 until words.size) print("${words[j].padEnd(results[j].length)} ")
    print("\nfull words:  ")
    for (j in 0 until results.size) print("${results[j]} ")
    println()
}