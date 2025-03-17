@file:OptIn(InternalAPI::class)

package co.early.fore.net.stub

import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import okio.FileSystem
import okio.SYSTEM
import readFileBytes
import kotlin.coroutines.CoroutineContext


/**
 * Note the OptIn at the top, ktor's HttpResponse.rawContent is subject to change (there
 * is no way to create a ktor HttpResponse instance without using internal apis as of 3.1.1).
 *
 * You might prefer to use ktor's MockEngine https://ktor.io/docs/client-testing.html (I
 * don't particularly like it, there are reasons to use stubs that aren't related to tests)
 */
class PluginStubInterceptor private constructor(
    private val forceStubMatch: Boolean,
    private val fileSystem: FileSystem,
    private val stubs: List<Pair<(HttpRequestBuilder) -> Boolean, Stub<*>>>,
    private val logger: Logger,
) {

    class Config {
        var forceStubMatch: Boolean = true  // i.e. crash if there is no matching stub
        var fileSystem = FileSystem.SYSTEM
        var stubs: List<Pair<(HttpRequestBuilder) -> Boolean, Stub<*>>> = emptyList()
        var logger: Logger? = null
    }


    companion object Plugin : HttpClientPlugin<Config, PluginStubInterceptor> {

        override val key = AttributeKey<PluginStubInterceptor>("PluginStubInterceptor")

        override fun prepare(block: Config.() -> Unit): PluginStubInterceptor {
            val config = Config().apply(block)
            return PluginStubInterceptor(
                forceStubMatch = config.forceStubMatch,
                fileSystem = config.fileSystem,
                stubs = config.stubs,
                logger = Fore.getLogger(config.logger)
            )
        }

        override fun install(plugin: PluginStubInterceptor, scope: HttpClient) {
            plugin.apply {
                scope.sendPipeline.intercept(HttpSendPipeline.Before) {

                    stubs.firstOrNull { it.first(context) }?.second?.let { stub ->

                        logger.i("[intercepting call]")

                        stub.throwable?.let { throw it } // simulates a connection failure

                        val responseBody = readFileBytes(
                            stub.bodyContentResourceFileName
                        )

                        proceedWith(object : HttpResponse() {
                            override val call: HttpClientCall = HttpClientCall(scope)
                            override val coroutineContext: CoroutineContext =
                                this@intercept.coroutineContext
                            override val headers: Headers = stub.headers
                            override val requestTime: GMTDate = GMTDate()
                            override val responseTime: GMTDate = GMTDate()
                            override val status: HttpStatusCode =
                                HttpStatusCode(stub.httpCode, stub.httpMessage)
                            override val version: HttpProtocolVersion = stub.protocol
                            override val rawContent: ByteReadChannel = ByteReadChannel(responseBody)
                        })

                    } ?: run {
                        if (forceStubMatch) {
                            throw Exception(
                                "No matching stub found AND forceStubMatch is true. " +
                                        "Set to false if you meant to let non matching HttpRequests " +
                                        "continue to make network calls as usual"
                            )
                        }
                        proceed()
                    }
                }
            }
        }
    }
}
