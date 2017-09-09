package scripts.implementation.test

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response

class RecordingHttpClient(private val delegate: HttpHandler): HttpHandler {
    val recording = HttpRecording()

    override fun invoke(request: Request): Response {
        val response = delegate(request)
        recording.record(request, response)
        return response
    }
}

data class HttpRecording(private val data: LinkedHashMap<Request, Response> = LinkedHashMap()) {
    fun record(request: Request, response: Response) {
        data.put(request, response)
    }

    fun replay() = object: HttpHandler {
        override fun invoke(request: Request): Response {
            return data[request] ?: error("No response recorded for request: $request")
        }
    }
}