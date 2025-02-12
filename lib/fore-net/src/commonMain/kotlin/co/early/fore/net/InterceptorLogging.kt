//package co.early.fore.net
//
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.util.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//// Define the custom Ktor logging plugin
//class InterceptorLogging private constructor(val logger: Logger) {
//
//    companion object : HttpClientPlugin<Unit, InterceptorLogging> {
//        override val key: AttributeKey<InterceptorLogging> = AttributeKey("CustomLoggingPlugin")
//
//        // Installing the plugin
//        override fun install(plugin: InterceptorLogging, scope: HttpClient) {
//            scope.sendPipeline.intercept(HttpRequestPipeline.State) { context ->
//                val request = context.request
//
//                // Log request method, URL, and headers
//                logger.log("Request: ${request.method.value} ${request.url}")
//                logger.log("Request Headers: ${request.headers}")
//
//                // Log request body if available
//                if (request.body != null) {
//                    logger.log("Request Body: ${request.body}")
//                }
//            }
//
//            scope.receivePipeline.intercept(HttpReceivePipeline.State) { context ->
//                val response = context.response
//
//                // Log response status and headers
//                logger.log("Response Status: ${response.status}")
//                logger.log("Response Headers: ${response.headers}")
//
//                // Log response body (if it can be read)
//                val responseBody = response.bodyAsText()
//                if (responseBody.isNotEmpty()) {
//                    logger.log("Response Body: $responseBody")
//                }
//            }
//        }
//
//        override fun prepare(block: Unit.() -> Unit): InterceptorLogging {
//            return InterceptorLogging(Logger.DEFAULT)
//        }
//    }
//}
//
//// Example Ktor Client setup with the custom logging plugin
//val client = HttpClient(CIO) {
//    install(InterceptorLogging) {
//        // You can specify your custom logger if needed
//    }
//}
//
//// Example request using the custom logging plugin
//suspend fun makeRequest() {
//    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts") {
//        header("Custom-Header", "Value")
//    }
//
//    println("Request complete!")
//}
//
//// Usage in your code
//suspend fun main() {
//    makeRequest()
//}
