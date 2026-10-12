// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "StravaKalcer",
    platforms: [
        .iOS(.v17),
        .macOS(.v14)
    ],
    products: [
        .library(name: "StravaKalcerCore", targets: ["StravaKalcerCore"]),
        .executable(name: "StravaKalcerCLI", targets: ["StravaKalcerCLI"])
    ],
    dependencies: [],
    targets: [
        .target(
            name: "StravaKalcerCore",
            dependencies: []
        ),
        .executableTarget(
            name: "StravaKalcerCLI",
            dependencies: ["StravaKalcerCore"]
        )
    ]
)
