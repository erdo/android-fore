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

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Shared.Versions.jvm_toolchain))
    }
}

android {

    namespace = appId
    compileSdk = Shared.Android.compileSdk

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Shared.Versions.composeCompiler
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
        implementation("co.early.fore:fore-kt-android:${Shared.Publish.published_fore_version_for_examples}")
        implementation("co.early.fore:fore-kt-android-compose:${Shared.Versions.composeCompiler}")
    } else {
        implementation(project(":fore-kt:fore-kt-android"))
        implementation(project(":fore-kt:fore-kt-android-compose"))
    }

    // persistence
    implementation("co.early.persista:persista:${Shared.Versions.perSista}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Shared.Versions.kotlinxSerializationJson}")

    // compose
    implementation("androidx.activity:activity-compose:${Shared.Versions.activityCompose}")
    implementation(platform("androidx.compose:compose-bom:${Shared.Versions.composeBom}"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //testing
    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")
}
