import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    kotlin("kapt")
}


val appId = "foo.bar.example.foreretrofitkt"

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
        implementation("co.early.fore:fore-kt-android-network:${Shared.Publish.published_fore_version_for_examples}")
    } else {
        implementation(project(":fore-kt:fore-kt-android-network"))
    }
    implementation("com.squareup.retrofit2:retrofit:${Shared.Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${Shared.Versions.converter_gson}")
    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")

    androidTestImplementation("io.mockk:mockk-android:${Shared.Versions.mockk}")
    androidTestImplementation("androidx.test:core:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test:runner:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test:rules:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test.ext:junit-ktx:${Shared.Versions.androidxjunit}")
    androidTestImplementation("androidx.annotation:annotation:${Shared.Versions.annotation}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}
