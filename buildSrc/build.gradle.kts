//import co.early.fore.Shared

plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {

  // we want the kotlin and android gradle plugin, because we want to access them in our plugin
  //implementation("com.android.tools.build:gradle:${co.early.fore.Shared.Versions.android_gradle_plugin}")
  implementation("com.android.tools.build:gradle:8.0.1")
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${co.early.fore.Shared.Versions.kotlin_version}")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")

  //for custom plugins
  implementation(gradleApi())
  implementation(localGroovy())
}

