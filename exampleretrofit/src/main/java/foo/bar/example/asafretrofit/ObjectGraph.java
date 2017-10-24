package foo.bar.example.asafretrofit;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.AndroidLogger;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.api.CustomGlobalErrorHandler;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;
import foo.bar.example.asafretrofit.message.UserMessage;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static co.early.asaf.core.Affirm.notNull;

/**
 * This is the price you pay for not using Dagger, the payback is not having to write modules
 */
class ObjectGraph {

    private volatile boolean initialized = false;
    private final Map<Class<?>, Object> dependencies = new HashMap<>();


    void setApplication(Application application) {
        setApplication(application, WorkMode.ASYNCHRONOUS);
    }

    void setApplication(Application application, final WorkMode workMode) {

        notNull(application);
        notNull(workMode);


        // create dependency graph
        AndroidLogger logger = new AndroidLogger();


        // networking classes
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://erdo.github.io/")
                .client(new OkHttpClient.Builder().build())
                .build();
        CallProcessor<UserMessage> callProcessor = new CallProcessor<UserMessage>(
                new CustomGlobalErrorHandler(logger),
                logger);


        // models
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                workMode);


        // add models to the dependencies map if you will need them later
        dependencies.put(FruitFetcher.class, fruitFetcher);

    }

    void init() {
        if (!initialized) {
            initialized = true;

            // run any necessary initialization code once object graph has been created here

        }
    }

    <T> T get(Class<T> model) {

        notNull(model);
        T t = model.cast(dependencies.get(model));
        notNull(t);

        return t;
    }

    <T> void putMock(Class<T> clazz, T object) {

        notNull(clazz);
        notNull(object);

        dependencies.put(clazz, object);
    }

}
