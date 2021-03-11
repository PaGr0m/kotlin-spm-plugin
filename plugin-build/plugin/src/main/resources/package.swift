// swift-tools-version:5.0

import PackageDescription

let package = Package(
    name: "CommandLineTool",
    targets: [
        .target(
            name: "CommandLineTool",
            dependencies: ["CommandLineToolCore"]
        ),
        .target(name: "CommandLineToolCore")
    ]
)