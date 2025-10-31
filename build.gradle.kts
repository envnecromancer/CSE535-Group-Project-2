plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.6.1" apply false

    // Kotlin plugins (all pinned to the SAME Kotlin version)
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.25" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.25" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.25" apply false
}
