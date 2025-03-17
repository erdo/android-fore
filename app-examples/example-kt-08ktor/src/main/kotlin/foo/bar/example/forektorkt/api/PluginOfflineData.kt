package foo.bar.example.forektorkt.api

import co.early.fore.net.stub.PluginStubInterceptor
import co.early.fore.net.stub.Stub
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.encodedPath
import io.ktor.http.headersOf

val offlineConfig: HttpClientConfig<*>.() -> Unit = {

    install(PluginStubInterceptor) {
        forceStubMatch = false
        stubs = listOf(
            { request: HttpRequestBuilder -> request.url.host.contains("mocky.io") } to Stub<Unit>(
                httpCode = 200,
                httpMessage = "OK",
                bodyContentResourceFileName = "fruit/success.json",
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
}