plugins {
    java
    kotlin("multiplatform")
    id("com.github.pagr0m.kotlin.native.spm")
}

kotlin {
    iosX64 {
        binaries {
            framework {
                baseName = "KotlinLibrary"
            }
        }
    }

    spm {
        ios("11") {
            dependencies {
                packages(
                    url = "https://github.com/AFNetworking/AFNetworking.git",
                    version = exactVersion("4.0.1"),
                    name = "AFNetworking"
                )
            }
        }
    }
}