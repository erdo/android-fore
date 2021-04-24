import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    id("com.android.application")
    id("maven")
    id("idea")
}


val appId = "foo.bar.example.foreadapters"

fun getTestBuildType(): String {
    return project.properties["testBuildType"] as String? ?: co.early.fore.Shared.BuildTypes.DEFAULT
}

println("[$appId testBuildType:${getTestBuildType()}]")


android {

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdkVersion(Shared.Android.compileSdkVersion)

    defaultConfig {
        applicationId = appId
        minSdkVersion(Shared.Android.minSdkVersion)
        targetSdkVersion(Shared.Android.targetSdkVersion)
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
    lintOptions {
        isAbortOnError = true
        lintConfig = File(project.rootDir, "lint-example-apps.xml")
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {

    if (Shared.Publish.use_published_version) {
        implementation("co.early.fore:fore-adapters-jv:${Shared.Publish.published_fore_version_for_examples}")
    } else {
        implementation(project(":fore-adapters-jv"))
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
    androidTestImplementation("androidx.test.ext:junit:${Shared.Versions.androidxjunit}")
    androidTestImplementation("androidx.test:runner:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.test:rules:${Shared.Versions.androidxtest}")
    androidTestImplementation("androidx.annotation:annotation:${Shared.Versions.annotation}")
    androidTestImplementation("androidx.core:core:${Shared.Versions.android_core}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Shared.Versions.espresso_core}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}
