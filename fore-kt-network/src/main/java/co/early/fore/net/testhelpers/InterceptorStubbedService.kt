package co.early.fore.net.testhelpers

import co.early.fore.kt.net.testhelpers.JunitFileIO
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.Response.Builder
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * Using this interceptor will simulate a canned server response at the okhttp level
 * without actually connecting to a real server
 *
 * This interceptor will easily work for models that are only calling one endpoint,
 * for more complex models with multiple end points returning different pojos
 * you would need slightly more sophistication here to intercept each network
 * request in a different way (you may also be able to construct the model under test
 * with a new Interceptor setup for each test).
 */
@Deprecated(message = "will be removed in the next major version", replaceWith = ReplaceWith(expression = "InterceptorStubOkHttp3", "co.early.fore.kt.net.testhelpers"))
class InterceptorStubbedService(
    private val stubbedServiceDefinition: StubbedServiceDefinition<*>
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        stubbedServiceDefinition.ioException?.let { //stubbing a connection failure
            throw stubbedServiceDefinition.ioException
        }
        val bodyString = JunitFileIO.getRawResourceAsUTF8String(
            stubbedServiceDefinition.resourceFileName,
            this.javaClass.classLoader
        )

        return Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(stubbedServiceDefinition.httpCode)
            .addHeader("Content-Type", stubbedServiceDefinition.mimeType)
            .body(bodyString.toResponseBody(stubbedServiceDefinition.mimeType.toMediaTypeOrNull()))
            .message("")
            .build()
    }
}
