// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Properties
import java.io.File

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.3")
        classpath("com.android.tools.build:gradle:4.0.0")  //android gradle plugin
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    }
}

plugins {
    id("idea")
}

//read in secrets file
val secrets = readProperties(File(project.rootDir, "secrets.properties"))

val AWS_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY") ?: secrets.getProperty("AWS_ACCESS_KEY")
val AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY") ?: secrets.getProperty("AWS_SECRET_KEY")


allprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        mavenLocal()

//  used to test rc builds - you won't have my key, but you can do something similar with your own amazon bucket
//        maven {
//            url "s3://com.ixpocket.repo.s3.eu-west-2.amazonaws.com/snapshots"
//            //url "s3://com.ixpocket.repo.s3.eu-west-2.amazonaws.com/releases"
//            credentials(AwsCredentials) {
//                accessKey AWS_ACCESS_KEY
//                secretKey AWS_SECRET_KEY
//            }
//        }

    }
}

ext.apply {
    set("kotlin_version", "1.3.72")
    set("minSdkVersion", 16)
    set("compileSdkVersion", 29)
    set("targetSdkVersion", 29)
    set("javaVersion", JavaVersion.VERSION_1_8)

    set("android_core", "1.1.0")
    set("annotation", "1.0.0")
    set("recyclerview", "1.1.0")
    set("material", "1.1.0")
    set("appcompat", "1.1.0")
    set("androidxtest", "1.1.0-beta02")
    set("androidxjunit", "1.1.1")
    set("room_runtime", "2.2.5")
    set("room_compiler", "2.2.0-rc01")
    set("room_testing", "2.2.0-rc01")
    set("espresso_core", "3.1.0-beta02")
    set("butterknife", "10.2.0")
    set("mockito_core", "2.23.0")
    set("mockk", "1.9.3")
    set("junit", "4.12")
    set("espresso", "3.0.2")
    set("hamcrest_library", "1.3")
    set("dexmaker_mockito", "2.19.1")
    set("robolectric", "4.3")
    set("gson", "2.8.5")
    set("constraintlayout", "1.1.3")
    set("coordinatorlayout", "1.1.0")
    set("cardview", "1.0.0")
    set("retrofit", "2.6.2")
    set("logging_interceptor", "3.12.0")
    set("converter_gson", "2.6.0")
    set("arrow_core", "0.10.3")
    set("core_ktx", "1.3.0")
    set("kotlinx_coroutines_core", "1.3.2")
    set("kotlinx_coroutines_android", "1.2.1")

    //LIB_VERSION_NAME="0.9.25-SNAPSHOT"
    set("LIB_VERSION_NAME", "1.1.3")
    set("LIB_VERSION_CODE", 41)

    set("REPO", "fore")
    set("LIB_GROUP", "co.early.fore")
    set("PROJ_NAME", "fore")
    set("LIB_DEVELOPER_ID", "erdo")
    set("LIB_DEVELOPER_NAME", "E Donovan")
    set("LIB_DEVELOPER_EMAIL", "eric@early.co")
    set("POM_PACKAGING", "aar")
    set("POM_URL", "https://erdo.github.io/android-fore/")
    set("POM_SCM_URL", "https://github.com/erdo/android-fore")
    set("POM_SCM_CONNECTION", "scm:git@github.com:erdo/android-fore.git")
    set("POM_SCM_DEV_CONNECTION", "scm:git@github.com:erdo/android-fore.git")

    set("LICENCE_SHORT_NAME", "Apache-2.0")
    set("LICENCE_NAME", "The Apache Software License, Version 2.0")
    set("LICENCE_URL", "http://www.apache.org/licenses/LICENSE-2.0.txt")

    set("ForeLibVersion_ForExamples", "1.1.3")

    set("BINTRAY_USER", System.getenv("BINTRAY_USER") ?: secrets.getProperty("BINTRAY_USER"))
    set("BINTRAY_API_KEY", System.getenv("BINTRAY_API_KEY") ?: secrets.getProperty("BINTRAY_API_KEY"))
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}

fun readProperties(propertiesFile: File): Properties {
    return Properties().apply {
        try {
            propertiesFile.inputStream().use { fis ->
                load(fis)
            }
        } catch (exception: Exception) {
            println("WARNING $propertiesFile not found! \n")
            println("exception: $exception \n")
        }
    }
}

