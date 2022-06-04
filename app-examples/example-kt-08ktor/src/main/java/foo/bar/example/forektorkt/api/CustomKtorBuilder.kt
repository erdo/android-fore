package foo.bar.example.forektorkt.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.Interceptor
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.json.serializer.KotlinxSerializer.Companion.DefaultJson

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
     * @param interceptors list of interceptors NB if you add a logging interceptor, it has to be
     * the last one in the list
     * @return ktor HttpClient object suitable for instantiating service interfaces
     */
    fun create(vararg interceptors: Interceptor): HttpClient {

        val okHttpConfig = OkHttp.create {
            for (interceptor in interceptors) {
                addInterceptor(interceptor)
            }
        }

        return HttpClient(okHttpConfig) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(DefaultJson)
            }
        }
    }
}
