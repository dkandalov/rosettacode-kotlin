package scripts.implementation.http

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XppDriver
import org.http4k.filter.cookie.BasicCookieStorage
import org.http4k.filter.cookie.CookieStorage
import org.http4k.filter.cookie.LocalCookie
import java.io.File

private val xStream = XStream(XppDriver())

class DiskCookieStorage(
    private val filePath: String,
    private var delegate: CookieStorage = loadFromFile(filePath)
): CookieStorage by delegate {

    override fun store(cookies: List<LocalCookie>) {
        delegate.store(cookies)
        saveToFile()
    }

    override fun remove(name: String) {
        delegate.remove(name)
        saveToFile()
    }

    private fun saveToFile() {
        val file = File(filePath)
        if (!file.exists()) file.createNewFile()
        file.writeText(xStream.toXML(delegate))
    }

    companion object {
        fun loadFromFile(filePath: String): CookieStorage {
            return try {
                xStream.fromXML(File(filePath).readText()) as CookieStorage
            } catch (e: Exception) {
                BasicCookieStorage()
            }
        }
    }
}