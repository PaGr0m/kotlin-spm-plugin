pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = ("kotlin-gradle-spm-plugin")

include(":example")
include(":untitled")
includeBuild("plugin-build")
