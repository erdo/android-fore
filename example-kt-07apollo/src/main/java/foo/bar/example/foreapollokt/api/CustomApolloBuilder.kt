package foo.bar.example.foreapollokt.api

import com.apollographql.apollo.ApolloClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @[co.early.fore.net.testhelpers.InterceptorStubbedService]
 *
 */
object CustomApolloBuilder {

    /**
     *
     * @param interceptors list of interceptors NB if you add a logging interceptor, it has to be
     * the last one in the list
     * @return ApolloClient object suitable for instantiating service interfaces
     */
    fun create(vararg interceptors: Interceptor): ApolloClient {

        return ApolloClient.builder()
                .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
                .okHttpClient(createOkHttpClient(*interceptors))
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
