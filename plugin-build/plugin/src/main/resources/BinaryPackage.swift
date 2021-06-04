// swift-tools-version:5.3

import PackageDescription

let package = Package(
    name: "ExampleWithKotlinLibrary",

    products: [
        .library(
            name: "ExampleWithKotlinLibrary",
            targets: [
                "ExampleWithKotlinLibrary",
                "KotlinLibrary"
        ]),
    ],

    targets: [
        .target(
            name: "ExampleWithKotlinLibrary",
            dependencies: []
        ),

        .binaryTarget(
            name: "KotlinLibrary",
            url: "$ARCHIVE_XCFRAMEWORK_LIBRARY",
            checksum: "$CHECK_SUM"
        ),
    ]
)
