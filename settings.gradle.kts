@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
            content {
                includeGroupByRegex("com.github.leavesCZY.*")
            }
        }
    }
}

rootProject.name = "Monitor"
include(":app")
include(":monitor")
include(":monitor-no-op")