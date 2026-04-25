import androidx.room3.gradle.RoomExtension
import github.leavesczy.monitor.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * @Author: leavesCZY
 * @Date: 2026/4/25 18:46
 * @Desc:
 */
class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "androidx.room3")
            apply(plugin = "com.google.devtools.ksp")
            extensions.configure<RoomExtension> {
                schemaDirectory(target.layout.buildDirectory.dir("schemas"))
            }
            dependencies {
                add("implementation", libs.findLibrary("androidx-room-runtime").get())
                add("ksp", libs.findLibrary("androidx-room-compiler").get())
                add("implementation", libs.findLibrary("androidx-room-paging").get())
            }
        }
    }

}