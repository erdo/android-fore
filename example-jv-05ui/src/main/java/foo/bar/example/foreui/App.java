package foo.bar.example.foreui;

import android.app.Application;

/**
 * Try not to fill this class with lots of code, if possible move it to a model somewhere
 */
public class App extends Application {

    private static App inst;

    @Override
    public void onCreate() {
        super.onCreate();

        inst = this;

        OG.setApplication(inst);
        OG.init();
    }

    public static App getInst() {
        return inst;
    }

}
