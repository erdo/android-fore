package foo.bar.example.foreapollokt.api

import co.early.fore.apollo.testhelpers.StubbedServiceDefinition
import foo.bar.example.foreapollokt.message.ErrorMessage
import java.io.IOException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked with: @[CustomGlobalErrorHandler]
 */
class CommonServiceFailures : ArrayList<StubbedServiceDefinition<ErrorMessage>>() {
    init {

        //network down
        add(StubbedServiceDefinition(IOException("fake io exception for testing purposes"), ErrorMessage.ERROR_NETWORK))

        //bad request
        add(StubbedServiceDefinition(400, "common/empty.json", ErrorMessage.ERROR_CLIENT))

        //session timeout
        add(StubbedServiceDefinition(401, "common/empty.json", ErrorMessage.ERROR_SESSION_TIMED_OUT))

        //missing resource
        add(StubbedServiceDefinition(404, "common/empty.json", ErrorMessage.ERROR_SERVER))//realise this is officially a "client" error, but in our experience this is usually the fault of the server

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
