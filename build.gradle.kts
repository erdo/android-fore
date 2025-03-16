/**
 *
 * ./release.sh ui|unit|both
 *
 * ./gradlew check
 *
 * ./gradlew test
 * ./gradlew testDebugUnitTest
 * ./gradlew testDebugUnitTest --warning-mode all
 *
 * for espresso tests: https://developer.android.com/training/testing/espresso/setup#set-up-environment
 *
 * ./gradlew connectedAndroidTest -PtestBuildType=debug --no-daemon --no-parallel --continue
 * ./gradlew connectedAndroidTest -PtestBuildType=debug --no-daemon --no-parallel --continue --warning-mode all
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
 * ./gradlew lib:fore-core:publishToMavenLocal
 * ./gradlew lib:fore-core:publishAllPublicationsToMavenCentralRepository --no-daemon --no-parallel
 *
 * // pre KMP ./gradlew publishReleasePublicationToMavenCentralRepository --no-daemon --no-parallel
 *
 * ./gradlew tasks --all | grep publish
 *
 * ./gradlew :fore-kt:fore-kt-android-compose:publishReleasePublicationToMavenCentralRepository --no-daemon --no-parallel
 *
 * ./gradlew :buildEnvironment
 * ./gradlew :fore-kt:fore-kt-core:dependencies
 *
 * ./gradlew :app-examples:example-kt-04retrofit:dependencies
 * ./gradlew -q :fore-kt:fore-kt-core:dependencyInsight --configuration compileClasspath --dependency okhttp3
 * ./gradlew lib:fore-core:dependencies --scan
 *
 * tag:fore_
 *
 * bundle exec jekyll serve
 *
 * bundle lock --update
 * bundle install
 *
 * git tag -a v1.5.9 -m 'v1.5.9'
 * git push origin --tags
 */

plugins {
    alias(libs.plugins.androidAppPlugin).apply(false)
    alias(libs.plugins.androidLibraryPlugin).apply(false)
    alias(libs.plugins.kotlinJvmPlugin).apply(false)
    alias(libs.plugins.kotlinAndroidPlugin).apply(false)
    alias(libs.plugins.kotlinMultiPlatformPlugin).apply(false)
    alias(libs.plugins.kotlinCocoapodsPlugin).apply(false)
    alias(libs.plugins.kotlinSerializationPlugin).apply(false)
    alias(libs.plugins.kotlinKaptPlugin).apply(false)
}
