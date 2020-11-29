import co.early.fore.Shared

plugins {
    id("com.android.application")
    id("maven")
    id("idea")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

//    signingConfigs {
//        create("release") {
//            storeFile = file("../../keystore/debug.keystore")
//            storePassword = "android"
//            keyAlias = "android"
//            keyPassword = "android"
//        }
//    }

    defaultConfig {
        applicationId = "foo.bar.example.foreapollokt"
        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
//          signingConfig = signingConfigs.getByName("release")
        }
    }
    lintOptions {
        isAbortOnError = true
        lintConfig = File(project.rootDir, "lint-example-apps.xml")
    }
}

repositories {
    jcenter()
    mavenCentral()
    google()
}

dependencies {

    //implementation("co.early.fore:fore-apollo-kt:${Shared.Versions.fore_version_for_examples}")
    implementation(project(":fore-apollo-kt"))

    implementation("com.squareup.retrofit2:converter-gson:${Shared.Versions.converter_gson}")
    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")


    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")

    //These tests need to be run on at least Android P / 9 / 27 (https://github.com/mockk/mockk/issues/182)
    androidTestImplementation("io.mockk:mockk-android:${Shared.Versions.mockk}") {
        exclude(module = "objenesis")
    }
    androidTestImplementation("org.objenesis:objenesis:2.6")
    //work around for https://github.com/mockk/issues/281
    androidTestImplementation("androidx.test:core:${Shared.Versions.androidxtestcore}")
    androidTestImplementation("androidx.test:runner:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test:rules:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test.ext:junit:${Shared.Versions.androidxjunit}")
    androidTestImplementation("androidx.annotation:annotation:${Shared.Versions.annotation}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}