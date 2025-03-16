package co.early.fore.net.stub

import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpProtocolVersion.Companion.HTTP_1_1
import io.ktor.http.headersOf

/**
 * When used in combination with [InterceptorStubOkHttp3] this class helps you junit test networking
 * code without making a real network call to fetch data. This class defines the details of the
 * stubbed http response you want to return to your code during a test.
 *
 * For instance, if you want to test how your code behaves when it receives an Http 404, you
 * can construct a [Stub] as follows:
 *
 * ```
 * Stub<Unit>(httpCode = 404)
 * ```
 *
 * If you want to test how your code behaves when it receives an HTTP 200, with a body content stored
 * locally in a resources file called "success_response.json", you can construct:
 *
 * ```
 * Stub<Unit>(httpCode = 200, bodyContentResourceFileName = "success_response.json")
 * ```
 *
 * To test the behaviour of your code when it encounters an IOException, you can construct:
 *
 * ```
 * Stub<Unit>(throwable = IOException())
 * ```
 *
 * You can specify the [httpCode], [httpMessage], [bodyContentResourceFileName] (the resource
 * file must be a UTF-8 encoded text file), [throwable], [headers], [protocol] (which defaults to
 * "http/1.1")
 *
 * As a convenience, you can also attach an expected value
 * [expectedResult] to the [Stub], which can later be asserted on, as follows:
 *
 * ```
 * val stub = Stub<Fruit>(httpCode = 200, expectedResult = Fruit())
 * // or
 * val stub = Stub<DataError>(httpCode = 404, expectedResult = DataError.NoFruitFound)
 *
 * // run the code that involves a network call, using the stub, and then...
 *
 * assertEquals(stub.expectedResult, actualResult)
 * ```
 */
class Stub<R>(
    val httpCode: Int = 0,
    val httpMessage: String = "",
    val bodyContentResourceFileName: String = "",
    val throwable: Throwable? = null,
    val headers: Headers = headersOf(), // headersOf("Content-Type" to listOf("application/json"))
    val protocol: HttpProtocolVersion = HTTP_1_1,
    val expectedResult: R? = null,
) {

    fun successfullyConnected() = (throwable == null)
}

typealias StubNoResult = Stub<Unit>
