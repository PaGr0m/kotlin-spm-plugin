object PluginCoordinates {
    const val GROUP = "com.github.pagr0m"
    const val ARTIFACT = "kotlin.native.spm"
    const val VERSION = "0.1.1"

    const val ID = "$GROUP.$ARTIFACT"
    const val IMPLEMENTATION_CLASS = "$ID.plugin.KotlinSpmPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/PaGr0m/kotlin-spm-plugin"
    const val WEBSITE = "https://github.com/PaGr0m/kotlin-spm-plugin"
    const val DESCRIPTION = "Gradle plugin for Swift Package Manager integration with Kotlin Multiplatform projects"
    const val DISPLAY_NAME = "Gradle Plugin for SPM integration with Kotlin MPP"

    val TAGS = listOf(
        "plugin",
        "gradle",
        "kotlin",
        "multiplatform",
        "spm"
    )
}

