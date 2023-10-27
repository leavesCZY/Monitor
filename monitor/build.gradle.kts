plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "github.leavesczy.monitor"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.google.material)
    implementation(libs.google.gson)
    compileOnly(libs.okhttp)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
            }
        }
    }
}