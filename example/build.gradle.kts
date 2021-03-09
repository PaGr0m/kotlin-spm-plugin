import org.zoldater.kotlin.gradle.spm.entity.impl.ProductManager.Library.LibraryType

plugins {
    java
    id("org.zoldater.kotlin.gradle.spm.plugin")

//    kotlin("multiplatform") version "1.4.255-SNAPSHOT"
}

//kotlin {
//    ios()
//}

spm {
    name = "Hello swift package manager plugin"

    dependencies {
        `package`("ExamplePackage")
    }

    targets {
        target("targetName") {
            dependencies {
                target("dependencyTargetName")
            }
        }
    }
}
