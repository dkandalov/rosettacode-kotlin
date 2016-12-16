package `stable_marriage_problem`

import java.util.*

class People(val map: Map<String, Array<String>>) {
    operator fun get(name: String) = map[name]

    val names: List<String> by lazy { map.keys.toList() }

    fun preferences(k: String, v: String): List<String> {
        val prefers = get(k)!!
        return ArrayList<String>(prefers.slice(0..prefers.indexOf(v)))
    }
}

class EngagementRegistry() : TreeMap<String, String>() {
    constructor(guys: People, girls: People) : this() {
        val freeGuys = guys.names.toMutableList()
        while (freeGuys.any()) {
            val guy = freeGuys.removeAt(0) // get a load of THIS guy
            val guy_p = guys[guy]!!
            for (girl in guy_p)
                if (this[girl] == null) {
                    this[girl] = guy  // girl is free
                    break
                } else {
                    val other = this[girl]!!
                    val girl_p = girls[girl]!!
                    if (girl_p.indexOf(guy) < girl_p.indexOf(other)) {
                        this[girl] = guy // this girl prefers this guy to the guy she's engaged to
                        freeGuys += other
                        break
                    } // else no change... keep looking for this guy
                }
        }
    }

    override fun toString(): String {
        val s = StringBuilder()
        for ((k, v) in this) s.append("$k is engaged to $v\n")
        return s.toString()
    }

    fun analyse(guys: People, girls: People) {
        if (check(guys, girls))
            println("Marriages are stable")
        else
            println("Marriages are unstable")
    }

    fun swap(girls: People, i: Int, j: Int) {
        val n1 = girls.names[i]
        val n2 = girls.names[j]
        val g0 = this[n1]!!
        val g1 = this[n2]!!
        this[n1] = g1
        this[n2] = g0
        println("$n1 and $n2 have switched partners")
    }

    private fun check(guys: People, girls: People): Boolean {
        val guy_names = guys.names
        val girl_names = girls.names
        if (!keys.containsAll(girl_names) or !values.containsAll(guy_names))
            return false

        val invertedMatches = TreeMap<String, String>()
        for ((k, v) in this) invertedMatches[v] = k

        for ((k, v) in this) {
            val sheLikesBetter = girls.preferences(k, v)
            val heLikesBetter = guys.preferences(v, k)
            for (guy in sheLikesBetter) {
                val fiance = invertedMatches[guy]
                val guy_p = guys[guy]!!
                if (guy_p.indexOf(fiance) > guy_p.indexOf(k)) {
                    println("$k likes $guy better than $v and $guy likes $k better than their current partner")
                    return false
                }
            }

            for (girl in heLikesBetter) {
                val fiance = get(girl)
                val girl_p = girls[girl]!!
                if (girl_p.indexOf(fiance) > girl_p.indexOf(v)) {
                    println("$v likes $girl better than $k and $girl likes $v better than their current partner")
                    return false
                }
            }
        }
        return true
    }
}

fun main(args: Array<String>) {
    val guys = People(mapOf("abe" to arrayOf("abi", "eve", "cath", "ivy", "jan", "dee", "fay", "bea", "hope", "gay"),
            "bob" to arrayOf("cath", "hope", "abi", "dee", "eve", "fay", "bea", "jan", "ivy", "gay"),
            "col" to arrayOf("hope", "eve", "abi", "dee", "bea", "fay", "ivy", "gay", "cath", "jan"),
            "dan" to arrayOf("ivy", "fay", "dee", "gay", "hope", "eve", "jan", "bea", "cath", "abi"),
            "ed" to arrayOf("jan", "dee", "bea", "cath", "fay", "eve", "abi", "ivy", "hope", "gay"),
            "fred" to arrayOf("bea", "abi", "dee", "gay", "eve", "ivy", "cath", "jan", "hope", "fay"),
            "gav" to arrayOf("gay", "eve", "ivy", "bea", "cath", "abi", "dee", "hope", "jan", "fay"),
            "hal" to arrayOf("abi", "eve", "hope", "fay", "ivy", "cath", "jan", "bea", "gay", "dee"),
            "ian" to arrayOf("hope", "cath", "dee", "gay", "bea", "abi", "fay", "ivy", "jan", "eve"),
            "jon" to arrayOf("abi", "fay", "jan", "gay", "eve", "bea", "dee", "cath", "ivy", "hope")))

    val girls = People(mapOf("abi" to arrayOf("bob", "fred", "jon", "gav", "ian", "abe", "dan", "ed", "col", "hal"),
            "bea" to arrayOf("bob", "abe", "col", "fred", "gav", "dan", "ian", "ed", "jon", "hal"),
            "cath" to arrayOf("fred", "bob", "ed", "gav", "hal", "col", "ian", "abe", "dan", "jon"),
            "dee" to arrayOf("fred", "jon", "col", "abe", "ian", "hal", "gav", "dan", "bob", "ed"),
            "eve" to arrayOf("jon", "hal", "fred", "dan", "abe", "gav", "col", "ed", "ian", "bob"),
            "fay" to arrayOf("bob", "abe", "ed", "ian", "jon", "dan", "fred", "gav", "col", "hal"),
            "gay" to arrayOf("jon", "gav", "hal", "fred", "bob", "abe", "col", "ed", "dan", "ian"),
            "hope" to arrayOf("gav", "jon", "bob", "abe", "ian", "dan", "hal", "ed", "col", "fred"),
            "ivy" to arrayOf("ian", "col", "hal", "gav", "fred", "bob", "abe", "ed", "jon", "dan"),
            "jan" to arrayOf("ed", "hal", "gav", "abe", "bob", "jon", "col", "ian", "fred", "dan")))

    with(EngagementRegistry(guys, girls)) {
        print(this)
        analyse(guys, girls)
        swap(girls, 0, 1)
        analyse(guys, girls)
    }
}