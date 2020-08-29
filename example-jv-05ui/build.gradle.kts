import co.early.fore.Shared

plugins {
    id("com.android.application")
    id("maven")
    id("idea")
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
        applicationId = "foo.bar.example.foreui"
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
        lintConfig = File(project.rootDir, "lint-examples.xml")
    }
}

repositories {
    jcenter()
    mavenCentral()
    google()
}

dependencies {

    annotationProcessor("com.jakewharton:butterknife-compiler:${Shared.Versions.butterknife}")
    //noinspection AnnotationProcessorOnCompilePath
    implementation("com.jakewharton:butterknife:${Shared.Versions.butterknife}")

    //implementation("co.early.fore:fore-lifecycle:${Shared.Versions.fore_version_for_examples}")
    implementation(project(":fore-lifecycle"))

    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("com.google.android.material:material:${Shared.Versions.material}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
}