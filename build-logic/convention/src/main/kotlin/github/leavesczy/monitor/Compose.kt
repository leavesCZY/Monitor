package github.leavesczy.monitor

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureCompose(commonExtension: CommonExtension) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }
        dependencies {
            val composeBom = libs.findLibrary("androidx-compose-bom").get()
            val composeBomPlatform = platform(composeBom)
            add("implementation", composeBomPlatform)
            add("implementation", libs.findLibrary("androidx-compose-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-foundation").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
        }
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            optIn.addAll(
                setOf(
                    "androidx.room3.ExperimentalRoomApi",
                    "androidx.paging.ExperimentalPagingApi",
                    "kotlinx.coroutines.DelicateCoroutinesApi"
                )
            )
        }
    }
}