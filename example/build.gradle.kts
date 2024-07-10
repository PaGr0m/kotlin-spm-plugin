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
        ios("13") {
            dependencies {
                packages(
                    url = "https://github.com/AFNetworking/AFNetworking.git",
                    version = "4.0.1",
                    name = "AFNetworking"
                )
                /* TODO: Support building target dependencies? i.e `FirebaseCore` & `FirebaseSharedSwift`
                packages(
                    url = "https://github.com/firebase/firebase-ios-sdk.git",
                    version = "10.21.0",
                    name = "FirebaseRemoteConfig"
                )
                */
                packages(
                    url = "https://github.com/Alamofire/Alamofire.git",
                    version = "5.2.0",
                    name = "Alamofire"
                )
                packages(
                    url = "https://github.com/malcommac/SwiftDate.git",
                    version = "7.0.0",
                    name = "SwiftDate"
                )
                packages(
                    url = "https://github.com/CombineCommunity/CombineExt.git",
                    version = "1.8.1",
                    name = "CombineExt"
                )
                packages(
                    url = "https://github.com/jozsef-vesza/AVFoundation-Combine.git",
                    version = "0.0.3",
                    name = "AVFoundationCombine"
                )
            }
        }
    }
}