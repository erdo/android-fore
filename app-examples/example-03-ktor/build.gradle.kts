import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    alias(libs.plugins.androidAppPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.kotlinKaptPlugin)
}


val appId = "foo.bar.example.forektorkt"

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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-example-kt-08ktor.pro")
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
        testImplementation(libs.fore.test.fixtures)
    } else {
        implementation(project(":lib:fore-core"))
        implementation(project(":lib:fore-net"))
        testImplementation(project(":lib:fore-test-fixtures"))
    }

    implementation("io.ktor:ktor-client-mock:${Shared.ExampleAppVersions.ktor_client}")
    implementation("io.ktor:ktor-client-cio:${Shared.ExampleAppVersions.ktor_client}")
    implementation("io.ktor:ktor-client-logging:${Shared.ExampleAppVersions.ktor_client}")
    implementation("io.ktor:ktor-client-okhttp:${Shared.ExampleAppVersions.ktor_client}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Shared.ExampleAppVersions.ktor_client}")
    implementation("io.ktor:ktor-client-content-negotiation:${Shared.ExampleAppVersions.ktor_client}")

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.ExampleAppVersions.constraintlayout}")

    implementation("org.slf4j:slf4j-nop:2.0.7") /// to get rid of the slf4 warning that comes from ktor

    testImplementation("junit:junit:${Shared.ExampleAppVersions.junit}")
    testImplementation("io.mockk:mockk:${Shared.ExampleAppVersions.mockk}")

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
