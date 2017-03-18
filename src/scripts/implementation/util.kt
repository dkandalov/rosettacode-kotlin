package scripts.implementation

val log: (Any?) -> Unit = {
    System.out.println(it)
}

private val xStream = com.thoughtworks.xstream.XStream(com.thoughtworks.xstream.io.xml.XppDriver())

/**
 * Caches result of function `f` in xml file.
 * The main reason for this is to speed up execution.
 *
 * To "invalidate" cached value modify or remove xml file.
 */
fun <T> cached(id: String, replace: Boolean = false, f: () -> T): T {
    val file = java.io.File(".cache/$id.xml")
    if (file.exists() && !replace) {
        log("// Using cached value of '$id'")
        @Suppress("UNCHECKED_CAST")
        return xStream.fromXML(file.readText()) as T
    } else {
        log("// Recalculating value of '$id'")
        val result = f()
        file.parentFile.mkdirs()
        file.writeText(xStream.toXML(result))
        return result
    }
}

fun clearLocalWebCache(vararg excluding: String = emptyArray()) {
    val cacheDir = ".cache"
    log(">>> Deleting files from $cacheDir")
    java.io.File(cacheDir).listFiles().filterNot { excluding.contains(it.name) }.forEach {
        val wasDeleted = it.delete()
        if (wasDeleted) {
            log("Deleted ${it.name}")
        } else {
            log("Failed to delete ${it.name}")
        }
    }
}

data class Progress(val i: Int, val total: Int) {
    fun next() = copy(i = i + 1)
    override fun toString() = "$i of $total"
}

fun <T, R> List<T>.mapParallelWithProgress(f: (T, Progress) -> R): List<R> {
    val progressRef = java.util.concurrent.atomic.AtomicReference(Progress(0, size))
    val poolSize = 20 // No particular reason for this number. Overall, the process is implied to be IO-bound.
    return java.util.concurrent.ForkJoinPool(poolSize).submit(java.util.concurrent.Callable {
        parallelStream().map {
            val progress = progressRef.updateAndGet { it.next() }
            f(it, progress)
        }.toList()
    }).get()
}

private fun <E> java.util.stream.Stream<E>.toList(): List<E> {
    return collect(java.util.stream.Collectors.toList<E>())
}