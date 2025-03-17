package foo.bar.example.forektorkt.api

import co.early.fore.core.delegate.Fore
import co.early.fore.net.NetworkingLogSanitizer
import co.early.fore.net.PluginNetworkLogs
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should also be able to use this class in your tests to mock the server
 * by passing offline data interceptor plugins in (see unit tests for an example)
 */
object KtorClientBuilder {

    /**
     *
     * @param configurePluginsBefore ktor plugin configuration block to be run first
     * @param configurePluginsAfter ktor plugin configuration block to be run  last
     * NB the logging plugin is usually the last one assuming you want to log what is actually
     * sent from the device after all the other plugins have run (an exception might be an offline
     * data plugin intercepting real requests)
     * @return ktor HttpClient object suitable for instantiating service interfaces
     */
    fun create(
        configurePluginsBefore: HttpClientConfig<*>.() -> Unit = {},
        configurePluginsAfter: HttpClientConfig<*>.() -> Unit = {},
    ): HttpClient {

        return HttpClient(CIO) {
            expectSuccess = true

            configurePluginsBefore(this)

            install(ContentNegotiation) {
                json()
            }
            install(PluginGlobalInterceptor) {
                logger = Fore.getLogger()
            }

            configurePluginsAfter(this)
        }
    }
}
