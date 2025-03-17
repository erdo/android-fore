package foo.bar.example.forektorkt.api

import co.early.fore.net.stub.Stub
import foo.bar.example.forektorkt.message.ErrorMessage.*
import io.ktor.http.headersOf
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * This will be specific to your own app, it's closely linked to: [GlobalErrorHandler]
 */
val CommonServiceFailures: List<Stub<*>> = listOf(
    //bad request
    Stub(400, "common/empty.json", expectedResult = ERROR_CLIENT),

    //session timeout
    Stub(401, "common/empty.json", expectedResult = ERROR_SESSION_TIMED_OUT),

    //missing resource
    Stub(404, "common/empty.json", expectedResult = ERROR_SERVER),

    //missing resource (html page)
    Stub(404,"common/html.json", headers = headersOf("Content-Type" to listOf("text/html")), expectedResult = ERROR_SERVER),

    //bad request
    Stub(405, "common/empty.json", expectedResult = ERROR_CLIENT),

    //server down
    Stub(500, "common/empty.json", expectedResult = ERROR_SERVER),

    //service unavailable
    Stub(503, "common/empty.json", expectedResult = ERROR_SERVER),

    //non valid json
    Stub(200, "common/html.json", expectedResult = ERROR_SERVER),

    //network down
    Stub(throwable = IOException("fake io exception for testing purposes"), expectedResult = ERROR_NETWORK),

    //network timeout
    Stub(throwable = SocketTimeoutException("fake timeout exception for testing purposes"), expectedResult = ERROR_NETWORK),
)
