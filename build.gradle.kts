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

plugins {
    id("idea")
}

allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
        //temporary while we wait for library maintainers to move out of jcenter
        maven { setUrl("$rootDir/jcenterlocal/repository/") }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
