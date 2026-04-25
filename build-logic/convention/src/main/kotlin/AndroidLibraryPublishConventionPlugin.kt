import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import github.leavesczy.monitor.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * @Author: leavesCZY
 * @Date: 2026/4/25 19:01
 * @Desc:
 */
class AndroidLibraryPublishConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.vanniktech.maven.publish")
            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral()
                signAllPublications()
                configure(platform = AndroidSingleVariantLibrary())
                coordinates(
                    groupId = "io.github.leavesczy",
                    version = libs.findVersion("leavesczy-monitor").get().toString()
                )
                pom {
                    name.set("Monitor")
                    description.set("An Http inspector for OkHttp & Retrofit")
                    inceptionYear.set("2025")
                    url.set("https://github.com/leavesCZY/Monitor")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("leavesCZY")
                            name.set("leavesCZY")
                            url.set("https://github.com/leavesCZY")
                        }
                    }
                    scm {
                        url.set("https://github.com/leavesCZY/Monitor")
                        connection.set("scm:git:git://github.com/leavesCZY/Monitor.git")
                        developerConnection.set("scm:git:ssh://git@github.com/leavesCZY/Monitor.git")
                    }
                }
            }
        }
    }
}