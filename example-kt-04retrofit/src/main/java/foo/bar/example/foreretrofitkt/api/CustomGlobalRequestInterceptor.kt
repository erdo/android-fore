package foo.bar.example.foreretrofitkt.api

import co.early.fore.core.logging.Logger
import co.early.fore.retrofit.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This will be specific to your own app.
 *
 * Typically you would construct this class with some kind of Session object or similar that
 * you would use to customize the headers according to the logged in status of the user, for example
 */
class CustomGlobalRequestInterceptor(
        private val logger: Logger /*, private final Session session */
) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val requestBuilder = original.newBuilder()


        requestBuilder.addHeader("content-type", "application/json")
        //requestBuilder.addHeader("X-MyApp-Auth-Token", !session.hasSession() ? "expired" : session.getSessionToken());
        requestBuilder.addHeader("User-Agent", "fore-example-user-agent-" + BuildConfig.VERSION_NAME)


        requestBuilder.method(original.method(), original.body())

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private val LOG_TAG = CustomGlobalRequestInterceptor::class.java.simpleName
    }

}
