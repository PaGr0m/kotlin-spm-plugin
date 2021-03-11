plugins {
    java
    id("org.zoldater.kotlin.gradle.spm.plugin")

    kotlin("multiplatform")
}

kotlin {
    ios()

    sourceSets {
        val commonMain by getting
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
//                `package`(
//                    url = "https://github.com/CocoaLumberjack/CocoaLumberjack.git",
//                    version = "3.7.0",
//                    name = "CocoaLumberjack"
//                )
            }
        }
    }
}
