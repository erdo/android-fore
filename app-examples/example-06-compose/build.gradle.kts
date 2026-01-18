import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    alias(libs.plugins.androidAppPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.composeCompilerPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.kotlinKaptPlugin)
}

val appId = "foo.bar.example.forecompose"

fun getTestBuildType(): String {
    return project.properties["testBuildType"] as String? ?: BuildTypes.DEFAULT
}

println("[$appId testBuildType:${getTestBuildType()}]")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }
}

android {

    namespace = appId
    compileSdk = Shared.Android.compileSdk

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = appId
        minSdk = Shared.Android.minComposeSdk
        targetSdk = Shared.Android.targetSdk
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testBuildType = getTestBuildType()
        multiDexEnabled = true
    }
    signingConfigs {
        create(BuildTypes.RELEASE) {
            // keytool -genkey -v -keystore debug.fake_keystore -storetype PKCS12 -alias android -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 20000 -dname "cn=Unknown, ou=Unknown, o=Unknown, c=Unknown"
            storeFile = file("../keystore/debug.fake_keystore")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }
    buildTypes {
        getByName(BuildTypes.DEBUG) {
            isMinifyEnabled = false
        }
        getByName(BuildTypes.RELEASE) {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../proguard-example-app.pro")
            signingConfig = signingConfigs.getByName(BuildTypes.RELEASE)
        }
    }
    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-example-apps.xml")
    }
}

dependencies {

    if (Shared.Publish.use_published_version) {
        implementation(libs.fore.core)
        implementation(libs.fore.net)
        implementation(libs.fore.compose)
        testImplementation(libs.fore.test.fixtures)
    } else {
        implementation(project(":lib:fore-core"))
        implementation(project(":lib:fore-net"))
        implementation(project(":lib:fore-compose"))
        testImplementation(project(":lib:fore-test-fixtures"))
    }

    // persistence
    implementation(libs.persista)
    implementation(libs.kotlinx.serialization)

    // compose

//    implementation("androidx.compose.ui:ui:1.6.0") // Update to latest
//    implementation("androidx.compose.runtime:runtime:1.6.0")

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.wear.compose.tooling)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.slf4j.nop) // to get rid of the slf4 warning that comes from ktor

    debugImplementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.wear.compose.tooling)
    debugImplementation(libs.androidx.wear.tooling.preview)

    //testing
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
