package foo.bar.example.foredb.api;

import java.io.IOException;

import co.early.fore.core.Affirm;
import co.early.fore.core.logging.Logger;
import co.early.fore.retrofit.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This will be specific to your own app.
 *
 * Typically you would construct this class with some kind of Session object or similar that
 * you would use to customize the headers according to the logged in status of the user, for example
 */
public class CustomGlobalRequestInterceptor implements Interceptor {

    private final static String TAG = CustomGlobalRequestInterceptor.class.getSimpleName();

    private final Logger logger;
    //private final Session session;

    public CustomGlobalRequestInterceptor(Logger logger) {
        this.logger = Affirm.notNull(logger);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder();


        requestBuilder.addHeader("content-type", "application/json");
        //requestBuilder.addHeader("X-MyApp-Auth-Token", !session.hasSession() ? "expired" : session.getSessionToken());
        requestBuilder.addHeader("User-Agent", "fore-example-user-agent-" + BuildConfig.VERSION_NAME);


        requestBuilder.method(original.method(), original.body());

        return chain.proceed(requestBuilder.build());
    }

}
