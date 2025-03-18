package foo.bar.example.foreapollo3.api

import co.early.fore.kt.net.testhelpers.Stub
import foo.bar.example.foreapollo3.message.ErrorMessage
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked with: [CustomGlobalErrorHandler]
 */
class CommonServiceFailures : ArrayList<Stub<ErrorMessage>>() {
    init {

        //bad request
        add(
            Stub(
                httpCode = 400,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_CLIENT
            )
        )

        //session timeout
        add(
            Stub(
                httpCode = 401,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_SESSION_TIMED_OUT
            )
        )

        //missing resource
        add(
            Stub(
                httpCode = 404,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_SERVER
            )
        )//realise this is officially a "client" error, but when this happens in prod, this is usually the fault of the server

        //missing resource (html page)
        add(
            Stub(
                httpCode = 404,
                bodyContentResourceFileName =  "common/html.json",
                headers = listOf(Stub.Header("Content-Type", "text/html")),
                expectedResult = ErrorMessage.ERROR_SERVER
            )
        )

        //bad request
        add(
            Stub(
                httpCode = 405,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_CLIENT
            )
        )

        //server down
        add(
            Stub(
                httpCode = 500,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_SERVER
            )
        )

        //service unavailable
        add(
            Stub(
                httpCode = 503,
                bodyContentResourceFileName = "common/empty.json",
                expectedResult = ErrorMessage.ERROR_SERVER
            )
        )

        //non valid json
        add(
            Stub(
                httpCode = 200,
                bodyContentResourceFileName = "common/html.json",
                expectedResult = ErrorMessage.ERROR_SERVER
            )
        )

        //network down / airplane mode
        add(
            Stub(
                throwable = IOException("fake io exception for testing purposes"),
                expectedResult = ErrorMessage.ERROR_NETWORK
            )
        )

        //network timeout
        add(
            Stub(
                throwable = SocketTimeoutException("fake timeout exception for testing purposes"),
                expectedResult = ErrorMessage.ERROR_NETWORK
            )
        )
    }
}
