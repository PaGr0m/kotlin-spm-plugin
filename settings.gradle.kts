pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = ("kotlin-gradle-spm-plugin")

include(":example")
include(":multiplatform-app-with-spm")
includeBuild("plugin-build")
