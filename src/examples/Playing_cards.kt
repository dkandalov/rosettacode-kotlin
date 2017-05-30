package `playing_cards`

// version 1.1.2

import java.util.Random

const val FACES = "23456789tjqka"
const val SUITS = "shdc"

val r = Random()

fun createDeck(): List<String> {
    val cards = mutableListOf<String>()
    for (suit in SUITS) {
        for (face in FACES) cards.add("$face$suit")
    }
    return cards
}

fun shuffleDeck(deck: List<String>): List<String> {
    val shuffled = mutableListOf<String>()
    do {
        val card = deck[r.nextInt(52)]
        if (card !in shuffled) shuffled.add(card)
    } while (shuffled.size < 52)
    return shuffled
}

fun dealTopDeck(deck: List<String>, n: Int) = deck.take(n)

fun dealBottomDeck(deck: List<String>, n: Int) = deck.takeLast(n).reversed()

fun printDeck(deck: List<String>) {
    for (i in 0 until deck.size) {
       print("${deck[i]}  ")
       if ((i + 1) % 13 == 0 || i == deck.size - 1) println()
    }
}

fun main(args: Array<String>) {
    var deck = createDeck()
    println("After creation, deck consists of:")
    printDeck(deck)
    deck = shuffleDeck(deck)
    println("\nAfter shuffling, deck consists of:")
    printDeck(deck)
    val dealtTop = dealTopDeck(deck, 10)
    println("\nThe 10 cards dealt from the top of the deck are:")
    printDeck(dealtTop)
    val dealtBottom = dealBottomDeck(deck, 10)
    println("\nThe 10 cards dealt from the bottom of the deck are:")
    printDeck(dealtBottom)
}