import co.early.fore.Shared
import co.early.fore.Shared.BuildTypes

plugins {
    id("com.android.application")
    id("com.apollographql.apollo3").version("3.8.1")
    kotlin("android")
    kotlin("kapt")
}


val appId = "foo.bar.example.foreapollo3"

fun getTestBuildType(): String {
    return project.properties["testBuildType"] as String? ?: BuildTypes.DEFAULT
}

println("[$appId testBuildType:${getTestBuildType()}]")


android {

    namespace = appId

    compileOptions {
        sourceCompatibility = Shared.Android.javaVersion
        targetCompatibility = Shared.Android.javaVersion
    }

    compileSdk = Shared.Android.compileSdk

    buildFeatures {
        viewBinding = true
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
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
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
        implementation("co.early.fore:fore-kt-android-core:${Shared.Publish.published_fore_version_for_examples}")
        implementation("co.early.fore:fore-kt-network:${Shared.Publish.published_fore_version_for_examples}")
    } else {
        implementation(project(":fore-kt:fore-kt-android-core"))
        implementation(project(":fore-kt:fore-kt-network"))
    }

    implementation("com.apollographql.apollo3:apollo-runtime:${Shared.Versions.apollo3}")

    implementation("io.coil-kt:coil:${Shared.Versions.coil}")
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
