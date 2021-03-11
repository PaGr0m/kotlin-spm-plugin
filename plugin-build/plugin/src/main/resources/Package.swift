// swift-tools-version:5.3

import PackageDescription

let package = Package(
    name: "$PLATFORM_NAME",
    platforms: [
        $PLATFORM_TYPE
    ],
    products: [
        .library(
            name: "$PLATFORM_NAME",
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
