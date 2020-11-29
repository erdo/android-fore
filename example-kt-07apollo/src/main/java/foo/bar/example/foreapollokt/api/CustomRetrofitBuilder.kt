package foo.bar.example.foreapollokt.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @[co.early.fore.retrofit.testhelpers.InterceptorStubbedService]
 *
 */
object CustomRetrofitBuilder {

    /**
     *
     * @param interceptors list of interceptors NB if you add a logging interceptor, it has to be
     * the last one in the list
     * @return Retrofit object suitable for instantiating service interfaces
     */
    fun create(vararg interceptors: Interceptor): Retrofit {

        return Retrofit.Builder()
            .baseUrl("http://www.mocky.io/v2/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(createOkHttpClient(*interceptors))
            .build()
    }

    private fun createOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {

        val builder = OkHttpClient.Builder()

        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }

        return builder.build()
    }

}
