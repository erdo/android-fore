package foo.bar.example.foreadapterskt

import android.app.Application

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
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
