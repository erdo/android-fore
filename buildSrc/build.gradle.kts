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
  sourceCompatibility = JavaVersion.VERSION_1_8
  // targetCompatibility = co.early.fore.Shared.Android.javaVersion
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    //jvmTarget = co.early.fore.Shared.Android.javaVersion.toString()
    jvmTarget = "1.8"
  }
}

dependencies {

  // we want the kotlin and android gradle plugin, because we want to access them in our plugin
  //implementation("com.android.tools.build:gradle:${co.early.fore.Shared.Versions.android_gradle_plugin}")
  implementation("com.android.tools.build:gradle:7.3.1")
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${co.early.fore.Shared.Versions.kotlin_version}")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")

  //for custom plugins
  implementation(gradleApi())
  implementation(localGroovy())
}

