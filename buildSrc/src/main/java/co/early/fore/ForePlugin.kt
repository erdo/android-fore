package co.early.fore

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import java.io.File

//https://quickbirdstudios.com/blog/gradle-kotlin-buildsrc-plugin-android/

class ForePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configurePlugins()
        project.configureAndroid()
    }
}

internal fun Project.configureAndroid() = this.extensions.getByType<LibraryExtension>().run {

    // android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

    lintOptions {
        isAbortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {

        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)

        versionCode = Shared.Publish.LIB_VERSION_CODE
        versionName = Shared.Publish.LIB_VERSION_NAME

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer.pro")
        }
    }
}

internal fun Project.configurePlugins() {
    plugins.apply("com.android.library")
}