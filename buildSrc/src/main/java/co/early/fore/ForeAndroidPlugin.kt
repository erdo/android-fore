package co.early.fore

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType
import java.io.File

class ForeAndroidPlugin : Plugin<Project> {
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

    compileSdk = Shared.Android.compileSdk

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {

        minSdk = Shared.Android.minSdk
        targetSdk = Shared.Android.targetSdk

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer.pro")
        }
    }

    //logSourceSets(sourceSets)

    //register (not create) - we want this to run after the rest of the android
    // block has been configured in the individual build files as they add files
    // to the source sets
    project.tasks.register("androidSourcesJar", Jar::class.java) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").java.srcDirs)
    }
}

fun logSourceSets(sourceSets: NamedDomainObjectContainer<AndroidSourceSet>) {
    println("\n**********")
    println("android sourceSets available:")
    sourceSets.names.forEach {
        println("-> $it")
    }
    println("...... listing contents of main only:")
    val mainSourceSet = sourceSets.getByName("main")
    mainSourceSet.java.srcDirs.forEach { dir ->
        print(dir.absolutePath)
        logFiles(dir)
        print("\n")
    }
    println("**********")
}

fun logFiles(file: File, space: String = ""){
    if (file.isDirectory){
        print("\n${space}${file.name}")
        file.listFiles()?.forEach {
            logFiles(it, "$space  ")
        }
    } else {
        print("\n${space}${file.name}")
    }
}

internal fun Project.configurePlugins() {
    plugins.apply("com.android.library")
}
