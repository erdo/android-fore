import java.util.Properties
import java.io.File

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

//read in secrets file
val secrets = readProperties(File(project.rootDir, "../secrets/secrets.properties"))

allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
        //temporary while we wait for library maintainers to move out of jcenter
        maven { url = uri("$rootDir/jcenterlocal/repository/") }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
ext.apply {

    // TODO remove these once we migrate publish.gradle to kts

    set("LIB_VERSION_NAME", "1.4.3")
    set("LIB_VERSION_CODE", 55)

    set("REPO", "fore")
    set("LIB_GROUP", "co.early.fore")
    set("PROJ_NAME", "fore")
    set("LIB_DEVELOPER_ID", "erdo")
    set("LIB_DEVELOPER_NAME", "E Donovan")
    set("LIB_DEVELOPER_EMAIL", "eric@early.co")
    set("POM_PACKAGING", "aar")
    set("POM_URL", "https://erdo.github.io/android-fore/")
    set("POM_SCM_URL", "https://github.com/erdo/android-fore")
    set("POM_SCM_CONNECTION", "scm:git@github.com:erdo/android-fore.git")
    set("POM_SCM_DEV_CONNECTION", "scm:git@github.com:erdo/android-fore.git")

    set("LICENCE_SHORT_NAME", "Apache-2.0")
    set("LICENCE_NAME", "The Apache Software License, Version 2.0")
    set("LICENCE_URL", "http://www.apache.org/licenses/LICENSE-2.0.txt")

    set("MAVEN_USER", System.getenv("MAVEN_USER") ?: secrets.getProperty("MAVEN_USER"))
    set("MAVEN_PASSWORD", System.getenv("MAVEN_PASSWORD") ?: secrets.getProperty("MAVEN_PASSWORD"))
    set("SONATYPE_STAGING_PROFILE_ID", System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: secrets.getProperty("SONATYPE_STAGING_PROFILE_ID"))

    set("SIGNING_KEY_ID", System.getenv("SIGNING_KEY_ID") ?: secrets.getProperty("SIGNING_KEY_ID"))
    set("SIGNING_PASSWORD", System.getenv("SIGNING_PASSWORD") ?: secrets.getProperty("SIGNING_PASSWORD"))
    set("SIGNING_KEY_RING_FILE", System.getenv("SIGNING_KEY_RING_FILE") ?: secrets.getProperty("SIGNING_KEY_RING_FILE"))
}

fun readProperties(propertiesFile: File): Properties {
    return Properties().apply {
        try {
            propertiesFile.inputStream().use { fis ->
                load(fis)
            }
        } catch (exception: Exception) {
            println("WARNING $propertiesFile not found! \n")
            println("exception: $exception \n")
        }
    }
}
