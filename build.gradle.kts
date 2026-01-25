plugins {
    alias(libs.plugins.android.application).apply(apply = false)
    alias(libs.plugins.android.library).apply(apply = false)
    alias(libs.plugins.kotlin.compose).apply(apply = false)
    alias(libs.plugins.androidx.room).apply(apply = false)
    alias(libs.plugins.google.ksp).apply(apply = false)
    alias(libs.plugins.maven.publish).apply(apply = false)
}