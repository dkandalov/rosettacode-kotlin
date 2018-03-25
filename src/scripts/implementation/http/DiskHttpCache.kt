package scripts.implementation.http

import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.parse
import org.http4k.traffic.ReadWriteCache
import scripts.implementation.ifNull
import java.io.File
import java.security.MessageDigest
import java.util.*

class DiskHttpCache(
    private val baseDir: String,
    private val shouldIgnore: (HttpMessage) -> Boolean = { true }
): ReadWriteCache {

    override fun set(request: Request, response: Response) {
        if (shouldIgnore(request) || shouldIgnore(response)) return

        val requestFolder = request.toFolder(baseDir.toBaseFolder())
        request.writeTo(requestFolder)
        response.writeTo(requestFolder)
    }

    override fun get(request: Request): Response? {
        if (shouldIgnore(request)) return null

        return File(request.toFolder(baseDir.toBaseFolder()), "response.txt").run {
            if (exists()) Response.parse(String(readBytes())) else null
        }
    }

    fun remove(f: (HttpMessage) -> Boolean) {
        baseDir.toBaseFolder().listFiles()
            .filter { it.isDirectory }
            .forEach { dir ->
                val request = File(dir, "request.txt").let { if (it.exists()) Request.parse(it.readText()) else null }
                val response = File(dir, "response.txt").let { if (it.exists()) Response.parse(it.readText()) else null }
                if ((request != null && f(request)) || (response != null && f(response))) {
                    dir.deleteRecursively()
                }
            }
    }

    fun clear() {
        File(baseDir)
            .listFiles().ifNull(emptyArray())
            .forEach {
                val wasDeleted = it.delete()
                if (!wasDeleted) error("Failed to delete ${it.name}")
            }
    }

    private fun String.toBaseFolder(): File = File(if (isEmpty()) "." else this)

    private fun Request.toFolder(baseDir: File) =
        File(File(baseDir, uri.path), String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA1").digest(toString().toByteArray())))
            .replace(File.separatorChar, '_'))

    private fun HttpMessage.writeTo(folder: File) {
        toFile(folder).apply {
            folder.mkdirs()
            createNewFile()
            writeBytes(this@writeTo.toString().toByteArray())
        }
    }

    private fun HttpMessage.toFile(folder: File): File = File(folder, if (this is Request) "request.txt" else "response.txt")
}