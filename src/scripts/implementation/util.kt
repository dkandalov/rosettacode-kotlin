package scripts.implementation

import org.http4k.core.Parameters
import org.http4k.core.Request
import org.http4k.core.body.form
import org.http4k.core.body.toBody
import java.util.concurrent.Callable
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicReference

val log: (Any?) -> Unit = {
    System.out.println(it)
}

fun Request.formData(parameters: Parameters) =
    run { body(form().plus(parameters).toBody()) }

data class Progress(val i: Int, val total: Int) {
    fun next() = copy(i = i + 1)
    override fun toString() = "$i of $total"
}

fun <T, R> List<T>.mapParallelWithProgress(f: (T, Progress) -> R): List<R> {
    val poolSize = 30 // No particular reason for this number. Overall, the process is assumed to be IO-bound.
    val executor = newFixedThreadPool(poolSize)

    val progressRef = AtomicReference(Progress(0, size))
    val futures = this.map {
        executor.submit(Callable {
            val progress = progressRef.updateAndGet { it.next() }
            f(it, progress)
        })
    }
    val result = futures.map { it.get() }
    executor.shutdown()
    return result
}

fun <T> T.ifNull(defaultValue: T): T = this ?: defaultValue