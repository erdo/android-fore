package foo.bar.example.forektorkt

import android.app.Application

finish fore-net-apollo
finish both sample apps w. basic unit tests only

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        inst = this

        OG.setApplication(this)
        OG.init()
    }

    companion object {
        lateinit var inst: App private set
    }
}
