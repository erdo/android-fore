import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    alias(libs.plugins.androidAppPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinKaptPlugin)
}

val appId = "foo.bar.example.forecoroutine"

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
        viewBinding = true
        buildConfig = true
    }
    defaultConfig {
        applicationId = appId
        minSdk = Shared.Android.minSdk
        targetSdk = Shared.Android.targetSdk
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testBuildType = getTestBuildType()
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
        testImplementation(libs.fore.test.fixtures)
    } else {
        implementation(project(":lib:fore-core"))
        testImplementation(project(":lib:fore-test-fixtures"))
    }

    implementation("androidx.appcompat:appcompat:1.5.1")

    testImplementation("junit:junit:${Shared.ExampleAppVersions.junit}")
    testImplementation("io.mockk:mockk:${Shared.ExampleAppVersions.mockk}")
    testImplementation(libs.kotlin.coroutines.test)

    androidTestImplementation("io.mockk:mockk-android:${Shared.ExampleAppVersions.mockk}")
    androidTestImplementation("androidx.test:core:${Shared.ExampleAppVersions.androidxtest}")
    androidTestImplementation("androidx.test:runner:${Shared.ExampleAppVersions.androidxtest}")
    androidTestImplementation("androidx.test:rules:${Shared.ExampleAppVersions.androidxtest}")
    androidTestImplementation("androidx.test.ext:junit-ktx:${Shared.ExampleAppVersions.androidxjunit}")
    androidTestImplementation("androidx.annotation:annotation:1.0.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.ExampleAppVersions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}
