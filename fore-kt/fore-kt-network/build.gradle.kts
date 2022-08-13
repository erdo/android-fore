import co.early.fore.Shared

plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = Shared.Android.javaVersion
    targetCompatibility = Shared.Android.javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = Shared.Android.javaVersion.toString()
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "fore-kt-network")
    set("LIB_DESCRIPTION", "fore - network code for kotlin")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

dependencies {
    api(project(":fore-kt:fore-kt-core"))
    api(project(":fore-kt:fore-kt-network-okhttp3v3x"))
    api(project(":fore-kt:fore-kt-network-okhttp3v4x"))
    compileOnly("com.squareup.okhttp3:okhttp:${Shared.Versions.okhttp3v3}")
    compileOnly("com.apollographql.apollo3:apollo-runtime:${Shared.Versions.apollo3}")
    // OkHttp3 v3.X.X used by Retrofit2 and Apollo2 has method calls: method(), body(), code() etc
    // OkHttp3 v4.X.X used by Ktor has fields: method, body, code etc instead
    // we use reflection so that we can handle either case in InterceptorLogging.kt
    implementation(kotlin("reflect"))
}

apply(from = "../../publish-lib.gradle.kts")
