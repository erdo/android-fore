import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
}


val appId = "foo.bar.example.forecompose"

fun getTestBuildType(): String {
    return project.properties["testBuildType"] as String? ?: BuildTypes.DEFAULT
}

println("[$appId testBuildType:${getTestBuildType()}]")


android {

    namespace = appId

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdk = Shared.Android.compileSdk

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
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Shared.Versions.composeCompiler
    }
}

dependencies {

    if (Shared.Publish.use_published_version) {
        implementation("co.early.fore:fore-kt-android:${Shared.Publish.published_fore_version_for_examples}")
        implementation("co.early.fore:fore-kt-android-compose:${Shared.Versions.composeCompiler}")
    } else {
        implementation(project(":fore-kt:fore-kt-android"))
        implementation(project(":fore-kt:fore-kt-android-compose"))
    }

    // persistence
    implementation("co.early.persista:persista:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // design
    implementation("androidx.compose.material3:material3:${Shared.Versions.material3}")

    // compose
    implementation("androidx.activity:activity-compose:${Shared.Versions.composeCompiler}")
    implementation("androidx.compose.ui:ui:${Shared.Versions.composeUi}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Shared.Versions.composeUi}")
    implementation("androidx.compose.ui:ui-tooling:${Shared.Versions.composeUi}")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:${Shared.Versions.composeUi}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Shared.Versions.composeUi}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Shared.Versions.composeUi}")

    //testing
    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")
}
