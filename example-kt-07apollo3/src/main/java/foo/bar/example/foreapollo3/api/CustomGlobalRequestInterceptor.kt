package foo.bar.example.foreapollo3.api

import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreapollo3.BuildConfig
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.feature.authentication.Authenticator.Companion.NO_SESSION
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This will be specific to your own app.
 */
@ExperimentalStdlibApi
class CustomGlobalRequestInterceptor(
        private val logger: Logger
) : Interceptor {

    private var authenticator: Authenticator? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val requestBuilder = original.newBuilder()


        requestBuilder.addHeader("Content-Type", "application/json")
        authenticator?.let {
            requestBuilder.addHeader("Authorization", if (it.sessionToken == NO_SESSION) "expired" else it.sessionToken)
        }
        requestBuilder.addHeader("User-Agent", "fore-example-user-agent-" + BuildConfig.VERSION_NAME)


        requestBuilder.method(original.method(), original.body())

        return chain.proceed(requestBuilder.build())
    }

    fun setAuthenticator(authenticator: Authenticator){
        this.authenticator = authenticator
    }

}
