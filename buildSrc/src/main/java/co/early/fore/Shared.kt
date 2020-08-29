package co.early.fore

import org.gradle.api.JavaVersion

object Shared {

    object Android {
        const val minSdkVersion = 16
        const val compileSdkVersion = 29
        const val targetSdkVersion = 29
        val javaVersion = JavaVersion.VERSION_1_8
    }

    object Versions {
        const val android_gradle_plugin = "4.0.1"
        const val kotlin_version = "1.4.0"
        const val dexcount_gradle_plugin = "0.8.3"
        const val gradle_bintray_plugin = "1.8.4"
        const val android_core = "1.1.0"
        const val annotation = "1.0.0"
        const val recyclerview = "1.1.0"
        const val material = "1.1.0"
        const val appcompat = "1.1.0"
        const val androidxtest = "1.1.0-beta02"
        const val androidxtestcore = "1.2.0"
        const val androidxjunit = "1.1.1"
        const val room_runtime = "2.2.5"
        const val room_compiler = "2.2.0-rc01"
        const val room_testing = "2.2.0-rc01"
        const val espresso_core = "3.1.0-beta02"
        const val butterknife = "10.2.0"
        const val mockito_core = "2.23.0"
        const val mockk = "1.9.3"
        const val junit = "4.12"
        const val espresso = "3.0.2"
        const val hamcrest_library = "1.3"
        const val dexmaker_mockito = "2.19.1"
        const val robolectric = "4.3"
        const val gson = "2.8.5"
        const val constraintlayout = "1.1.3"
        const val coordinatorlayout = "1.1.0"
        const val cardview = "1.0.0"
        const val retrofit = "2.6.2"
        const val logging_interceptor = "3.12.0"
        const val converter_gson = "2.6.0"
        const val arrow_core = "0.10.3"
        const val core_ktx = "1.3.0"
        const val kotlinx_coroutines_core = "1.3.2"
        const val kotlinx_coroutines_android = "1.2.1"
        const val fore_version_for_examples = "1.1.3"
    }

    object Publish {
        //LIB_VERSION_NAME="0.9.25-SNAPSHOT"
        const val LIB_VERSION_NAME = "1.1.3"
        const val LIB_VERSION_CODE = 41
        const val REPO = "fore"
        const val LIB_GROUP = "co.early.fore"
        const val PROJ_NAME = "fore"
        const val LIB_DEVELOPER_ID = "erdo"
        const val LIB_DEVELOPER_NAME = "E Donovan"
        const val LIB_DEVELOPER_EMAIL = "eric@early.co"
        const val POM_PACKAGING = "aar"
        const val POM_URL = "https://erdo.github.io/android-fore/"
        const val POM_SCM_URL = "https://github.com/erdo/android-fore"
        const val POM_SCM_CONNECTION = "scm:git@github.com:erdo/android-fore.git"
        const val POM_SCM_DEV_CONNECTION = "scm:git@github.com:erdo/android-fore.git"
        const val LICENCE_SHORT_NAME = "Apache-2.0"
        const val LICENCE_NAME = "The Apache Software License, Version 2.0"
        const val LICENCE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    }
}
