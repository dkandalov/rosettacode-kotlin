package scripts.implementation.test

import io.kotlintest.specs.StringSpec
import org.http4k.core.*

class RecordReplayHttpSpec: StringSpec() {
    private val someRequest = Request(Method.GET, Uri.of("some-uri"))
    private val someResponse = Response(Status.OK)

    init {
        "record request-response pair" {
            val httpClient = { _: Request -> someResponse }
            val recordingHttpClient = RecordingHttpClient(httpClient)

            recordingHttpClient(someRequest)

            recordingHttpClient.recording shouldEqual HttpRecording(linkedMapOf(
                someRequest to someResponse
            ))
        }

        "replay responses" {
            val httpRecording = HttpRecording(linkedMapOf(
                someRequest to someResponse
            ))
            val replayingHttpClient = httpRecording.replay()

            replayingHttpClient(someRequest) shouldEqual someResponse
        }
    }
}