package foo.bar.example.foreapollo.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.ktorClient

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @[co.early.fore.net.testhelpers.InterceptorStubbedService]
 *
 */
object ApolloClientBuilder {

    fun create(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
            .ktorClient(KtorClientBuilder.create())
            .build()
    }
}
