plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.library.publish)
}

android {
    namespace = "github.leavesczy.monitor"
}

mavenPublishing {
    coordinates(artifactId = "monitor-no-op")
}

dependencies {
    compileOnly(libs.squareup.okHttp)
}