pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = ("kotlin-gradle-spm-plugin")

include(":example")
includeBuild("plugin-build")
