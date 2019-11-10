package foo.bar.example.foreretrofitcoroutine


import android.app.Application
import co.early.fore.core.Affirm
import co.early.fore.core.WorkMode

/**
 * Try not to fill this class with lots of code, if possible move it to a model somewhere
 *
 * Copyright © 2018 early.co. All rights reserved.
 */
class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()

        inst = this

        objectGraph = ObjectGraph()
        objectGraph.setApplication(this)
    }

    fun injectSynchronousObjectGraph() {
        objectGraph = ObjectGraph()
        objectGraph.setApplication(this, WorkMode.SYNCHRONOUS)
    }

    fun <T> injectMockObject(clazz: Class<T>, instance: T) {
        objectGraph.putMock(clazz, instance)
    }

    companion object {

        lateinit var inst: CustomApp private set
        private lateinit var objectGraph: ObjectGraph

        fun init() {

            // run any initialisation code here

        }

        /**
         * This is how dependencies get injected, typically an Activity/Fragment/View will call this
         * during the onCreate()/onCreateView()/onFinishInflate() method respectively for each of the
         * dependencies it needs.
         *
         *
         * Can use the dagger library for similar behaviour using annotations
         *
         *
         * Will return mocks if they have been injected previously in injectMockObject()
         *
         *
         * Call it like this:  YourModel yourModel =
         * CustomApp.get(YourModel.class);
         *
         *
         * If you want to more tightly scoped object, one way is to pass a factory class here and create
         * an instance where you need it
         *
         * @param s
         * @return
         */
        operator fun <T> get(s: Class<T>): T {
            Affirm.notNull(objectGraph)
            return objectGraph.get(s)
        }
    }

}
