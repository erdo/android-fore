import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    id("com.android.application")
    id("maven")
    id("idea")
    id("com.apollographql.apollo").version("2.5.2")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}


val appId = "foo.bar.example.foreapollokt"

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

    if (Shared.Publish.use_published_version) {
        implementation("co.early.fore:fore-network-kt:${Shared.Publish.published_fore_version_for_examples}")
    } else {
        implementation(project(":fore-network-kt"))
    }

    implementation("com.apollographql.apollo:apollo-runtime:${Shared.Versions.apollo}")

    implementation("io.coil-kt:coil:${Shared.Versions.coil}")
    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")

    testImplementation("junit:junit:${Shared.Versions.junit}")
    testImplementation("io.mockk:mockk:${Shared.Versions.mockk}")

    //These tests need to be run on at least Android P / 9 / 27 (https://github.com/mockk/mockk/issues/182)
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
