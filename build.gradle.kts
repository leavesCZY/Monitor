plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.androidx.room).apply(false)
    alias(libs.plugins.google.ksp).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
}