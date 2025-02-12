package foo.bar.example.forektorkt.api

import co.early.fore.net.PluginLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.*

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @[co.early.fore.net.testhelpers.InterceptorStubbedService]
 *
 */
object CustomKtorBuilder {

    /**
     *
     * @param plugins list of ktor plugins NB if you add a logging plugin, it has to be
     * the last one in the list
     * @return ktor HttpClient object suitable for instantiating service interfaces
     */
    fun create(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                this.json()
            }

            // CustomGlobalRequestInterceptor(logger),
            install(Logging) {
                level = LogLevel.ALL
                filter { request ->
                    request.url.host.contains("ktor.io")
                }
            }
            install(PluginLogging) {
                this.logger = logger
            }
        }
    }
}
