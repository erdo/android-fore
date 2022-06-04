package foo.bar.example.forektorkt.api

import co.early.fore.net.testhelpers.StubbedServiceDefinition
import foo.bar.example.forektorkt.message.ErrorMessage
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked with: @[CustomGlobalErrorHandler]
 */
class CommonServiceFailures : ArrayList<StubbedServiceDefinition<ErrorMessage>>() {
    init {

        //network down
        add(StubbedServiceDefinition(IOException("fake io exception for testing purposes"), ErrorMessage.ERROR_NETWORK))

        //network timeout
        add(StubbedServiceDefinition(SocketTimeoutException("fake timeout exception for testing purposes"), ErrorMessage.ERROR_NETWORK))

        //bad request
        add(StubbedServiceDefinition(400, "common/empty.json", ErrorMessage.ERROR_CLIENT))

        //session timeout
        add(StubbedServiceDefinition(401, "common/empty.json", ErrorMessage.ERROR_SESSION_TIMED_OUT))

        //missing resource
        add(StubbedServiceDefinition(404, "common/empty.json", ErrorMessage.ERROR_SERVER))

        //missing resource (html page)
        add(StubbedServiceDefinition(404, "common/html.json", "text/html", ErrorMessage.ERROR_SERVER))

        //bad request
        add(StubbedServiceDefinition(405, "common/empty.json", ErrorMessage.ERROR_CLIENT))

        //server down
        add(StubbedServiceDefinition(500, "common/empty.json", ErrorMessage.ERROR_SERVER))

        //service unavailable
        add(StubbedServiceDefinition(503, "common/empty.json", ErrorMessage.ERROR_SERVER))

        //non valid json
        add(StubbedServiceDefinition(200, "common/html.json", ErrorMessage.ERROR_SERVER))

    }

}
