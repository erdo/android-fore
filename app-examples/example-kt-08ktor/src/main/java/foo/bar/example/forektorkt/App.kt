package foo.bar.example.forektorkt

import androidx.multidex.MultiDexApplication

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class App : MultiDexApplication() {

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
