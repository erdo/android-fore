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

sourceSets["main"].java.apply {
    srcDirs(
        "../fore-jv-network/src/main/java"
    )
    exclude(
        "co/early/fore/net/InterceptorLogging.java"
    )
}

dependencies {

    api(project(":fore-kt-core"))
    compileOnly("com.apollographql.apollo3:apollo-runtime:${Shared.Versions.apollo3}")

    // OkHttp3 v3.X.X used by Retrofit2 and Apollo2 has method calls: method(), body(), code() etc
    // OkHttp3 v4.X.X used by Ktor has fields: method, body, code etc instead
    // we use reflection so that we can handle either case in InterceptorLogging.kt
    implementation(kotlin("reflect"))
}

apply(from = "../publish-lib.gradle.kts")
