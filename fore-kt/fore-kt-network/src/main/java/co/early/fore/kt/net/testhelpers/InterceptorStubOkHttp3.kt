package co.early.fore.kt.net.testhelpers

import co.early.fore.kt.net.testhelpers.okhttp3v3x.v3xResponse
import co.early.fore.kt.net.testhelpers.okhttp3v4x.v4xResponse
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Using this interceptor will simulate a canned server response at the okhttp level
 * without connecting to a real server
 *
 * This interceptor will easily work for models that are only calling one endpoint,
 * for more complex models with multiple end points returning different pojos
 * you would need slightly more sophistication here to intercept each network
 * request in a different way (you may also be able to construct the model under test
 * with a new Interceptor setup for each test).
 */
class InterceptorStubOkHttp3(
    private val stub: Stub<*>
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {

        stub.throwable?.let { throw it } // simulates a connection failure

        val bodyString = JunitFileIO.getRawResourceAsUTF8String(
            stub.bodyContentResourceFileName,
            this.javaClass.classLoader
        )

        return when (determineOkHttp3Version()){
            OkHttp3Version.V3x -> {
                v3xResponse(
                    request = chain.request(),
                    protocol = stub.protocol,
                    httpCode = stub.httpCode,
                    body = bodyString,
                    message = stub.httpMessage,
                    headers = stub.headers.map { it.name to it.value }
                )
            }
            OkHttp3Version.V4x -> {
                v4xResponse(
                    request = chain.request(),
                    protocol = stub.protocol,
                    httpCode = stub.httpCode,
                    body = bodyString,
                    message = stub.httpMessage,
                    headers = stub.headers.map { it.name to it.value }
                )
            }
        }
    }

    private fun determineOkHttp3Version(): OkHttp3Version {

        val functionsDetected = (Request::class.members.find {
                it.name == "method"
            }?.toString()?.startsWith("fun") ?: false)

        return if (functionsDetected){
            OkHttp3Version.V3x
        } else {
            OkHttp3Version.V4x
        }
    }

    private sealed class OkHttp3Version {
        object V3x: OkHttp3Version()
        object V4x: OkHttp3Version()
    }
}
