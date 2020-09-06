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
        applicationId = "foo.bar.example.foredb"
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

    //implementation("co.early.fore:fore-jv:${Shared.Versions.fore_version_for_examples}")
    implementation(project(":fore-jv"))

    kapt("androidx.room:room-compiler:${Shared.Versions.room_compiler}")
    kapt("com.jakewharton:butterknife-compiler:${Shared.Versions.butterknife}")
    //noinspection AnnotationProcessorOnCompilePath
    implementation("com.jakewharton:butterknife:${Shared.Versions.butterknife}")

    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("com.google.android.material:material:${Shared.Versions.material}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")
    implementation("com.squareup.retrofit2:converter-gson:${Shared.Versions.converter_gson}")
    implementation("androidx.room:room-runtime:${Shared.Versions.room_runtime}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Shared.Versions.kotlin_version}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("androidx.room:room-testing:${Shared.Versions.room_testing}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")
    testImplementation("org.robolectric:robolectric:${Shared.Versions.robolectric}") {
        exclude(module = "maven-artifact")
    }

    androidTestImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:${Shared.Versions.dexmaker_mockito}")
    androidTestImplementation("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    androidTestImplementation("androidx.test:runner:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test:rules:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.annotation:annotation:${Shared.Versions.annotation}")
    androidTestImplementation("androidx.core:core:${Shared.Versions.android_core}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}
