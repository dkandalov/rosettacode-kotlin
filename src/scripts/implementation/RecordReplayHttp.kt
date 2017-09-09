package scripts.implementation

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response

class RecordingHttpClient(private val httpHandler: HttpHandler, val recording: HttpRecording = HttpRecording()): HttpHandler {
    override fun invoke(request: Request): Response {
        val response = recording.responseFor(request) ?: httpHandler(request)
        recording.record(request, response)
        return response
    }
}

data class HttpRecording(private val data: LinkedHashMap<Request, Response> = LinkedHashMap()) {
    fun record(request: Request, response: Response) {
        data.put(request, response)
    }

    fun responseFor(request: Request): Response? = data[request]
}