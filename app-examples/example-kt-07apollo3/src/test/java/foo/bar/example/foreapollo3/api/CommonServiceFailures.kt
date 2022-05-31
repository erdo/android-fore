package foo.bar.example.foreapollo3.api

import co.early.fore.net.testhelpers.StubbedServiceDefinition
import foo.bar.example.foreapollo3.message.ErrorMessage
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked with: @[CustomGlobalErrorHandler]
 */
@ExperimentalStdlibApi
class CommonServiceFailures : ArrayList<co.early.fore.net.testhelpers.StubbedServiceDefinition<ErrorMessage>>() {
    init {

        //network down / airplane mode
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                IOException("fake io exception for testing purposes"),
                ErrorMessage.ERROR_NETWORK
            )
        )

        //network timeout
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                SocketTimeoutException("fake timeout exception for testing purposes"),
                ErrorMessage.ERROR_NETWORK
            )
        )

        //bad request
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                400,
                "common/empty.json",
                ErrorMessage.ERROR_CLIENT
            )
        )

        //session timeout
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                401,
                "common/empty.json",
                ErrorMessage.ERROR_SESSION_TIMED_OUT
            )
        )

        //missing resource
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                404,
                "common/empty.json",
                ErrorMessage.ERROR_SERVER
            )
        )//realise this is officially a "client" error, but when this happens in prod, this is usually the fault of the server

        //missing resource (html page)
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                404,
                "common/html.json",
                "text/html",
                ErrorMessage.ERROR_SERVER
            )
        )

        //bad request
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                405,
                "common/empty.json",
                ErrorMessage.ERROR_CLIENT
            )
        )

        //server down
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                500,
                "common/empty.json",
                ErrorMessage.ERROR_SERVER
            )
        )

        //service unavailable
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                503,
                "common/empty.json",
                ErrorMessage.ERROR_SERVER
            )
        )

        //non valid json
        add(
            co.early.fore.net.testhelpers.StubbedServiceDefinition(
                200,
                "common/html.json",
                ErrorMessage.ERROR_SERVER
            )
        )

    }

}
