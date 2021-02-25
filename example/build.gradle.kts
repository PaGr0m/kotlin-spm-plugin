import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.ProductManager.Library.LibraryType

plugins {
    java
    id("org.zoldater.kotlin.gradle.spm.plugin")
}

spm {
    name = "Hello swift package manager plugin"

    platforms {
        iOS("12")
    }

    dependencies {
        `package`("ExamplePackage")
        `package`("ExamplePackage", "1.3")
    }

    products {
        executable("productName") {
            +"myPackage1"
            +"myPackage2"
            +"myPackage3"
        }

        library("staticLibraryName", LibraryType.STATIC) {
            +"myLib1"
            +"myLib2"
        }

        library("dynamicLibraryName", LibraryType.DYNAMIC) {
            +"myLib1"
            +"myLib2"
            +"myLib3"
        }
    }

    targets {
        // Regular target area
        target("targetName") {
            path = "./src/myPackage"

            dependencies {
                target("dependencyTargetName")
                target(name = "dependencyTargetName")
                target(name = "dependencyTargetName", condition = "TODO")
                target("asd")

                product("package", "package-kit")
                product(name = "package", `package` = "package-kit")
                product(name = "package", `package` = "package-kit", condition = "TODO")
            }

            exclude {
                +"foo.swift"
                +"bar.swift"
            }

            sources {
                +"main.swift"
                +"utils.swift"
            }

            resources {
                process("Resources/config.json")
                copy("Resources/HTML")
            }
        }

        // Executable target area
        executableTarget("executableTargetName") {
            // Same as `regular target`
        }

        // Test target area
        testTarget("testTargetName") {
            // Same as `regular target`
        }

        // System library area
        systemLibrary("systemLibraryName") {
            path = "./src/lib"
        }

        binaryTarget(
            "SomeRemoteBinaryPackage",
            "https://url/to/some/remote/binary/package.zip"
        )

        binaryTarget(
            name = "SomeLocalBinaryPackage",
            path = "path/to/some.xcframework"
        )
    }
}
