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
        macos("11") {
            dependencies {
                `package`(
                    url = "https://github.com/AFNetworking/AFNetworking.git",
                    version = "4.0.0",
                    name = "AFNetworking"
                )
                `package`(
                    url = "https://github.com/Alamofire/Alamofire.git",
                    version = "5.2.0",
                    name = "Alamofire"
                )
            }
        }

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