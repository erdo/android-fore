/**
 *
 * ./gradlew check
 *
 * ./gradlew testDebugUnitTest
 * ./gradlew connectedAndroidTest -PtestBuildType=debug --no-daemon --no-parallel --continue
 * ./gradlew connectedAndroidTest -PtestBuildType=release
 * ./gradlew app-examples:example-kt-01reactiveui:connectedAndroidTest -PtestBuildType=release --info
 * ./gradlew app-examples:example-kt-01reactiveui:testDebugUnitTest --info
 * ./gradlew app-examples:example-kt-04retrofit:dependencies --configuration releaseRuntimeClasspath
 *
 * sourcefile downloads sanity check:
 * co.early.fore.kt.net.apollo.CallProcessorApollo() //fore-kt-android-network
 * co.early.fore.net.NetworkingLogSanitizer() //fore-jv-network
 * co.early.fore.kt.net.NetworkingLogSanitizer() //fore-kt-network
 * co.early.fore.kt.adapters.NotifyableImp() //fore-kt-android-adapters
 * co.early.fore.adapters.CrossFadeRemover() //fore-jv-android-adapters
 * co.early.fore.kt.core.delegate.DebugDelegateDefault() //fore-kt-android-core
 * co.early.fore.kt.core.logging.SystemLogger() //fore-kt-core
 * co.early.fore.core.testhelpers.CountDownLatchWrapper() //fore-jv-core
 *
 * ./gradlew clean
 * ./gradlew publishToMavenLocal
 * ./gradlew publishReleasePublicationToMavenCentralRepository --no-daemon --no-parallel
 *
 * ./gradlew :buildEnvironment
 * ./gradlew :fore-kt-core:dependencies
 * ./gradlew :app-examples:example-kt-04retrofit:dependencies
 *
 * ./gradlew -q dependencyInsight --dependency okhttp3
 *
 * bundle exec jekyll serve
 *
 * bundle lock --update
 * bundle install
 *
 * git tag -a v1.5.9 -m 'v1.5.9'
 * git push origin --tags
 */
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${co.early.fore.Shared.Versions.kotlin_version}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${co.early.fore.Shared.Versions.kotlin_version}")
        classpath("com.android.tools.build:gradle:${co.early.fore.Shared.Versions.android_gradle_plugin}")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
