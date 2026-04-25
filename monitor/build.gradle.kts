plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.android.compose)
    alias(libs.plugins.app.androidx.room)
    alias(libs.plugins.app.library.publish)
}

android {
    namespace = "github.leavesczy.monitor"
}

mavenPublishing {
    coordinates(artifactId = "monitor")
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.google.gson)
    compileOnly(libs.squareup.okHttp)
}