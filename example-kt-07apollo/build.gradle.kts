import co.early.fore.Shared

plugins {
    id("com.android.application")
    id("maven")
    id("idea")
    id("com.apollographql.apollo").version("2.4.5")
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

apollo {
    generateKotlinModels.set(true)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
}
// fetch the graphql schema:
//./gradlew :example-kt-07apollo:downloadApolloSchema -Pcom.apollographql.apollo.endpoint='https://apollo-fullstack-tutorial.herokuapp.com/' -Pcom.apollographql.apollo.schema='src/main/graphql/foo/bar/example/foreapollokt/graphql/schema.json'


repositories {
    jcenter()
    mavenCentral()
    google()
}

dependencies {

    implementation(project(":fore-network-kt"))
    //implementation("co.early.fore:fore-network-kt:${Shared.Versions.fore_version_for_examples}")

    implementation("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")

    implementation("io.coil-kt:coil:${Shared.Versions.coil}")
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
