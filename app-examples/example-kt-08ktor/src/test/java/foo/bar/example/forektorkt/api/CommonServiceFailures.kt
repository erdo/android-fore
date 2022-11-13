package foo.bar.example.forektorkt.api

import co.early.fore.kt.net.testhelpers.Stub
import foo.bar.example.forektorkt.message.ErrorMessage
import foo.bar.example.forektorkt.message.ErrorMessage.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked to: [CustomGlobalErrorHandler]
 */
class CommonServiceFailures : ArrayList<Stub<ErrorMessage>>() {
    init {

        //bad request
        add(Stub(400, "common/empty.json", expectedResult = ERROR_CLIENT))

        //session timeout
        add(Stub(401, "common/empty.json", expectedResult = ERROR_SESSION_TIMED_OUT))

        //missing resource
        add(Stub(404, "common/empty.json", expectedResult = ERROR_SERVER))

        //missing resource (html page)
        add(Stub(404, "common/html.json", headers = listOf(Stub.Header("Content-Type", "text/html")), expectedResult = ERROR_SERVER))

        //bad request
        add(Stub(405, "common/empty.json", expectedResult = ERROR_CLIENT))

        //server down
        add(Stub(500, "common/empty.json", expectedResult = ERROR_SERVER))

        //service unavailable
        add(Stub(503, "common/empty.json", expectedResult = ERROR_SERVER))

        //non valid json
        add(Stub(200, "common/html.json", expectedResult = ERROR_SERVER))

        //network down
        add(Stub(throwable = IOException("fake io exception for testing purposes"), expectedResult = ERROR_NETWORK))

        //network timeout
        add(Stub(throwable = SocketTimeoutException("fake timeout exception for testing purposes"), expectedResult = ERROR_NETWORK))
    }
}
