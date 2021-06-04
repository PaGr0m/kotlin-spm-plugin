plugins {
    java
    kotlin("multiplatform")
    id("com.github.pagr0m.kotlin.native.spm")
}

kotlin {
    iosX64()

    spm {
        ios("11") {
            dependencies {
                packages(
                    url = "https://github.com/johnsundell/files.git",
                    version = "4.0.0",
                    name = "Files"
                )
            }
        }
    }
}