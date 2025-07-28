import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "github.leavesczy.monitor"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

dependencies {
    compileOnly(libs.squareup.okHttp)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
    coordinates(
        groupId = "io.github.leavesczy",
        artifactId = "monitor-no-op",
        version = libs.versions.monitor.publishing.get()
    )
    pom {
        name = "Monitor"
        description = "An Http inspector for OkHttp & Retrofit"
        inceptionYear = "2025"
        url = "https://github.com/leavesCZY/Monitor"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "leavesCZY"
                name = "leavesCZY"
                url = "https://github.com/leavesCZY"
            }
        }
        scm {
            url = "https://github.com/leavesCZY/Monitor"
            connection = "scm:git:git://github.com/leavesCZY/Monitor.git"
            developerConnection = "scm:git:ssh://git@github.com/leavesCZY/Monitor.git"
        }
    }
}