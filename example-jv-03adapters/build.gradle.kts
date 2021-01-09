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

    signingConfigs {
        create("release") {
            // keytool -genkey -v -keystore debug.fake_keystore -storetype PKCS12 -alias android -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 20000 -dname "cn=Unknown, ou=Unknown, o=Unknown, c=Unknown"
            storeFile = file("../keystore/debug.fake_keystore")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "foo.bar.example.foreadapters"
        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../proguard-example-app.pro")
            signingConfig = signingConfigs.getByName("release")
            testBuildType = "release"
        }
        getByName("debug") {
            isMinifyEnabled = false
            testBuildType = "debug"
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

    if (Shared.Publish.use_published_version) {
        implementation("co.early.fore:fore-adapters:${Shared.Publish.published_fore_version_for_examples}")
    } else {
        implementation(project(":fore-adapters"))
    }

    annotationProcessor("com.jakewharton:butterknife-compiler:${Shared.Versions.butterknife}")
    //noinspection AnnotationProcessorOnCompilePath
    implementation("com.jakewharton:butterknife:${Shared.Versions.butterknife}")

    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.recyclerview:recyclerview:${Shared.Versions.recyclerview}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Shared.Versions.mockito_core}")
    testImplementation("org.hamcrest:hamcrest-library:${Shared.Versions.hamcrest_library}")

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
