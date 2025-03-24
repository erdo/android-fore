import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    alias(libs.plugins.androidAppPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.kotlinKaptPlugin)
    alias(libs.plugins.apolloPlugin)
}

val appId = "foo.bar.example.foreapollo"

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
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-example-kt-08ktor.pro"
            )
            signingConfig = signingConfigs.getByName(BuildTypes.RELEASE)
        }
    }
    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-example-apps.xml")
    }
}

apollo {
    service("apollo-fullstack-tutorial") {
        packageName.set(appId)
    }
}

// fetch the graphql schema:
// ./gradlew :app-examples:example-kt-07apollo3:downloadApolloSchema --endpoint='https://apollo-fullstack-tutorial.herokuapp.com/graphql' --schema='app-examples/example-kt-07apollo3/src/main/graphql/schema.json'
//
// dev.to article
// https://dev.to/erdo/android-apollo3-and-graphql-1e8m
//
// creating graphql queries:
// https://www.youtube.com/watch?v=omSpI1Nu_pg

dependencies {

    if (Shared.Publish.use_published_version) {
        implementation(libs.fore.core)
        implementation(libs.fore.net)
        implementation(libs.fore.net.apollo)
        testImplementation(libs.fore.test.fixtures)
        testImplementation(libs.fore.net.apollo.test.fixtures)
    } else {
        implementation(project(":lib:fore-core"))
        implementation(project(":lib:fore-net"))
        implementation(project(":lib:fore-net-apollo"))
        testImplementation(project(":lib:fore-test-fixtures"))
        testImplementation(project(":lib:fore-net-apollo-test-fixtures"))
    }

    implementation(libs.apollo.runtime)
    implementation(libs.apollo.engine.ktor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.content.negotiation)

    implementation("io.coil-kt:coil:${Shared.Versions.coil}")
    implementation("androidx.appcompat:appcompat:${Shared.Versions.appcompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Shared.Versions.constraintlayout}")

    implementation("org.slf4j:slf4j-nop:2.0.7") // to get rid of the slf4 warning that comes from ktor

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
