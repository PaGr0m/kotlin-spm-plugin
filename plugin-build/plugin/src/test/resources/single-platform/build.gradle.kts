plugins {
    java
    kotlin("multiplatform")
    id("com.pagrom.kotlin.gradle.spm.plugin")
}

kotlin {
    iosX64()

    spm {
        ios("11") {
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