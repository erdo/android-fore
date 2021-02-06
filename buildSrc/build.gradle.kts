//import co.early.fore.Shared

plugins {
    id("org.gradle.kotlin.kotlin-dsl").version("1.4.9")
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
  jcenter()
}

dependencies {

  // we want the kotlin and android gradle plugin, because we want to access them in our plugin
  //implementation("com.android.tools.build:gradle:${co.early.fore.Shared.Versions.android_gradle_plugin}")
  implementation("com.android.tools.build:gradle:4.1.2")
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Shared.Versions.kotlin_version}")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")

  //for custom plugins
  implementation(gradleApi())
  implementation(localGroovy())
}

