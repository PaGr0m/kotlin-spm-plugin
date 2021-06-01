plugins {
    kotlin("multiplatform")
    id("com.github.pagr0m.kotlin.native.spm")
}

kotlin {
    iosX64().binaries.framework {
        baseName = "KotlinLibrary"
    }
    spm.ios("11") {
        dependencies {
            packages(
                "github.com/AFNetworking/AFNetworking.git",
                "4.0.0", "AFNetworking"
            )
        }
    }
}
