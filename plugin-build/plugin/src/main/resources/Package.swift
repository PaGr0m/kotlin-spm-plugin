// swift-tools-version:5.6

import PackageDescription

let package = Package(
    name: "$PLATFORM_NAME",
    platforms: [
        $PLATFORM_TYPE("$PLATFORM_VERSION")
    ],
    products: [
        .library(
            name: "$PLATFORM_NAME",
            // type: .dynamic,
            targets: ["$PLATFORM_NAME"]),
    ],
    dependencies: [
        $DEPENDENCIES
    ],
    targets: [
        .target(
            name: "$PLATFORM_NAME",
            dependencies: [
                $TARGET_DEPENDENCY
            ]
        ),
    ]
)
