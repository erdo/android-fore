package foo.bar.example.asafthreading;

import android.app.Application;

import co.early.asaf.framework.Affirm;
import co.early.asaf.framework.WorkMode;

/**
 * Try not to fill this class with lots of code, if possible move it to a model somewhere
 */
public class CustomApp extends Application {

    private static CustomApp instance;
    private static ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (objectGraph == null) {
            objectGraph = new ObjectGraph();
        }
        objectGraph.setApplication(this);
    }

    public static CustomApp getInstance() {
        return instance;
    }


    public void injectSynchronousObjectGraph() {
        objectGraph = new ObjectGraph();
        objectGraph.setApplication(this, WorkMode.SYNCHRONOUS);
    }

    public <T> void injectMockObject(Class<T> clazz, T object) {
        objectGraph.putMock(clazz, object);
    }

    // unfortunately the android test runner calls Application.onCreate() once _before_ we get a
    // chance to call createApplication() in ApplicationTestCase (contrary to the documentation).
    // So to prevent initialisation stuff happening before we have had a chance to set our mocks
    // during tests, we need to separate out the init() stuff, which is why we put it here,
    // to be called by the base activity of the app
    // http://stackoverflow.com/questions/4969553/how-to-prevent-activityunittestcase-from-calling-application-oncreate
    public static void init() {
        if (objectGraph != null) {
            objectGraph.init();
        }
    }

    /**
     * This is how dependencies get injected, typically an Activity/Fragment/View will call this
     * during the onCreate()/onCreateView()/onFinishInflate() method respectively for each of the
     * dependencies it needs.
     * <p/>
     * Can use the dagger library for similar behaviour using annotations
     * <p/>
     * Will return mocks if they have been injected previously in injectMockObject()
     * <p/>
     * Call it like this: </br> YourModel yourModel =
     * CustomApp.get(YourModel.class);
     * <p>
     * If you want to more tightly scoped object, pass a factory class here and create an instance
     * where you need it
     *
     * @param s
     * @return
     */
    public static <T> T get(Class<T> s) {
        Affirm.notNull(objectGraph);
        return objectGraph.get(s);
    }

}
