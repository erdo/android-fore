package foo.bar.example.forektorkt.api

import co.early.fore.core.delegate.Fore
import co.early.fore.net.NetworkingLogSanitizer
import co.early.fore.net.PluginNetworkLogs
import co.early.fore.net.stub.PluginStubInterceptor
import co.early.fore.net.stub.Stub
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.*

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @[co.early.fore.net.testhelpers.InterceptorStubbedService]
 *
 */
object KtorClientBuilder {

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
                json()
            }
            install(PluginGlobalInterceptor) {
                logger = Fore.getLogger()
            }
            install(PluginStubInterceptor) {
                stubs = listOf(
                    { request: HttpRequestBuilder -> request.url.encodedPath == "/users" } to Stub<Unit>(
                        httpCode = 200,
                        httpMessage = "OK",
                        bodyContentResourceFileName = "users.json",
                        headers = headersOf("Content-Type" to listOf("application/json"))
                    ),

                    { request: HttpRequestBuilder -> request.url.encodedPath == "/non-existent" } to Stub(
                        httpCode = 404,
                        httpMessage = "Not Found",
                        bodyContentResourceFileName = "error_404.json",
                        headers = headersOf("Content-Type" to listOf("application/json")),
                    ),

                    { request: HttpRequestBuilder ->
                        request.method.value == "POST" && request.headers["Authorization"] == "Bearer token123"
                    } to Stub(
                        httpCode = 201,
                        httpMessage = "Created",
                        bodyContentResourceFileName = "post_response.json",
                        headers = headersOf("Content-Type" to listOf("application/json"))
                    ),

                    { request: HttpRequestBuilder -> request.url.encodedPath == "/server-error" } to Stub(
                        httpCode = 500,
                        httpMessage = "Internal Server Error",
                        throwable = RuntimeException("Simulated server failure"),
                        headers = headersOf("Content-Type" to listOf("application/json"))
                    )
                )
            }
            install(PluginNetworkLogs) {
                // all these are optional, default will suit most requirements
                curlStyleRequestLogs = true
                prettifyResponseLogs = true
                networkingLogSanitizer = object : NetworkingLogSanitizer {
                    override fun sanitizeHeaders(headers: Set<Map.Entry<String, List<String>>>): Set<Map.Entry<String, List<String>>> {
                        return headers
                            .filterNot { header ->
                                setOf(
                                    "Authorization",
                                    "X-Auth-Token",
                                    "X-Session-Token"
                                ).any { it.equals(header.key, ignoreCase = true) }
                            }
                            .toSet()
                    }

                    override fun sanitizeBody(text: String): String {
                        // highly implementation specific, e.g. you might want
                        // to recursively search through json keys for "id" or whatever
                        // and redact all the corresponding values etc
                        return text.replace("secret", "XXXX")
                    }
                }
                filters = listOf { request ->
                    // don't log any hosts containing notinteresting.com
                    !request.url.host.contains("notinteresting.com")
                }
            }
        }
    }
}
