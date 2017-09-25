package scripts.implementation

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import org.http4k.core.*

class RecordReplayHttpSpec: StringSpec() {
    private val someRequest1 = Request(Method.GET, Uri.of("some-uri 1"))
    private val someRequest2 = Request(Method.GET, Uri.of("some-uri 2"))
    private val someResponse = Response(Status.OK)

    init {
        "record request-response for which there is no recorded response" {
            val httpClient = { _: Request -> someResponse }
            val recordingHttpClient = RecordingHttpClient(httpClient, HttpRecording())

            recordingHttpClient(someRequest1)
            recordingHttpClient(someRequest2)

            recordingHttpClient.recording shouldEqual HttpRecording(linkedMapOf(
                someRequest1 to someResponse,
                someRequest2 to someResponse
            ))
        }

        "replay response if there is recorded response" {
            val httpRecording = HttpRecording(linkedMapOf(
                someRequest1 to someResponse,
                someRequest2 to someResponse
            ))
            val recordingHttpClient = RecordingHttpClient({ error("") }, httpRecording)

            recordingHttpClient(someRequest1) shouldEqual someResponse
            recordingHttpClient(someRequest2) shouldEqual someResponse
        }
    }
}