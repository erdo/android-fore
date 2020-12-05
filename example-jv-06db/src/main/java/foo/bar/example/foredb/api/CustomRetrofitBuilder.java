package foo.bar.example.foredb.api;

import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Most of this will all be specific to your application, when customising for your own case
 * bare in mind that you should be able to use this class in your tests to mock the server
 * by passing different interceptors in:
 *
 * see @{@link co.early.fore.net.testhelpers.InterceptorStubbedService}
 *
 */
public class CustomRetrofitBuilder {

    /**
     *
     * @param interceptors list of interceptors NB if you add a logging interceptor, it has to be
     *                     the last one in the list
     * @return Retrofit object suitable for instantiating service interfaces
     */
    public static Retrofit create(Interceptor... interceptors){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io/v2/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .client(createOkHttpClient(interceptors))
                .build();

        return retrofit;
    }

    private static OkHttpClient createOkHttpClient(Interceptor... interceptors){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }

}
