pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = ("kotlin-gradle-spm-plugin")

include(":example")
includeBuild("plugin-build")
