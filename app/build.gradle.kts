plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.android.compose)
}

android {
    namespace = "github.leavesczy.monitor.samples"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.squareup.okHttp.logging.interceptor)
//    debugImplementation(libs.leavesczy.monitor)
//    releaseImplementation(libs.leavesczy.monitor.no.op)
    debugImplementation(project(":monitor"))
    releaseImplementation(project(":monitor-no-op"))
}