import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "github.leavesczy.monitor.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.androidx.room.gradle)
    compileOnly(libs.google.ksp.gradle)
    compileOnly(libs.maven.publish.gradle)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.app.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.app.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.app.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidxRoom") {
            id = libs.plugins.app.androidx.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidPublish") {
            id = libs.plugins.app.android.publish.get().pluginId
            implementationClass = "AndroidPublishConventionPlugin"
        }
    }
}