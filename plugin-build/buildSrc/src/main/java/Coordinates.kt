object PluginCoordinates {
    const val ID = "org.zoldater.kotlin.gradle.spm.plugin"
    const val GROUP = "org.zoldater.kotlin.gradle.spm"
    const val VERSION = "1.0.0"
    const val IMPLEMENTATION_CLASS = "org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/zoldater/kotlin-spm-plugin"
    const val WEBSITE = "https://github.com/zoldater/kotlin-spm-plugin"
    const val DESCRIPTION = "Gradle plugin for Swift Package Manager integration with Kotlin Multiplatform projects"
    const val DISPLAY_NAME = "Gradle Plugin for SPM integration with Kotlin MPP projects"
    val TAGS = listOf(
        "plugin",
        "gradle",
        "kotlin",
        "multiplatform",
        "spm"
    )
}

