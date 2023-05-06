//import co.early.fore.Shared

plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

java {
  // sourceCompatibility = co.early.fore.Shared.Android.javaVersion
  sourceCompatibility = JavaVersion.VERSION_11
  // targetCompatibility = co.early.fore.Shared.Android.javaVersion
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    //jvmTarget = co.early.fore.Shared.Android.javaVersion.toString()
    jvmTarget = "11"
  }
}

dependencies {

  // we want the kotlin and android gradle plugin, because we want to access them in our plugin
  //implementation("com.android.tools.build:gradle:${co.early.fore.Shared.Versions.android_gradle_plugin}")
  implementation("com.android.tools.build:gradle:7.4.0")
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${co.early.fore.Shared.Versions.kotlin_version}")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")

  //for custom plugins
  implementation(gradleApi())
  implementation(localGroovy())
}

