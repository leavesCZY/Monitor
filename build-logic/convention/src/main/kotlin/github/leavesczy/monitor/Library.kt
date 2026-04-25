package github.leavesczy.monitor

import com.android.build.api.dsl.LibraryExtension
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2026/4/25 18:34
 * @Desc:
 */
internal fun configureAndroidLibrary(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        defaultConfig.apply {
            consumerProguardFiles.add(File("consumer-rules.pro"))
        }
    }
}