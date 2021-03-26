plugins {
    java
    kotlin("multiplatform")
    id("org.zoldater.kotlin.gradle.spm.plugin")
}

kotlin {
    iosX64()

    sourceSets {
        val commonMain by getting
        val iosX64Main by getting
    }

    spm {
        ios("11") {
            name = "ios example"
            dependencies {
                `package`(
                    url = "https://github.com/johnsundell/files.git",
                    version = "4.0.0",
                    name = "Files"
                )
            }
        }
    }
}