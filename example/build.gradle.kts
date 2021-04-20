plugins {
    java
    kotlin("multiplatform")
    id("org.zoldater.kotlin.gradle.spm.plugin")
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

    sourceSets {
        val commonMain by getting
        val iosArm64Main by getting
        val macosX64Main by getting
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

        macos("11") {
            name = "tvos example"
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
    }
}