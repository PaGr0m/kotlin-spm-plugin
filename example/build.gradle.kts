plugins {
    java
    kotlin("multiplatform")
    id("com.pagrom.kotlin.gradle.spm.plugin")
}

kotlin {
    iosArm64 {
        binaries {
            framework {
                baseName = "KlibIOS"
            }
        }
    }

    macosX64 {
        binaries {
            framework {
                baseName = "KlibMacOS"
            }
        }
    }

    spm {
        macos("10") {
            dependencies {
                `package`(
                    url = "https://github.com/AFNetworking/AFNetworking.git",
                    version = "4.0.0",
                    name = "AFNetworking"
                )
            }
        }

        ios("11") { }
    }
}