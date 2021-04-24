include(":fore-core-jv")
include(":fore-core-kt")
include(":fore-core-android-jv")
include(":fore-core-android-kt")
include(":fore-adapters-jv")
include(":fore-adapters-kt")
include(":fore-network-jv")
include(":fore-network-kt")
include(":fore-jv")
include(":fore-kt")

include(":example-kt-01reactiveui", ":example-kt-02coroutine", "example-kt-03adapters",
        ":example-kt-04retrofit", ":example-kt-07apollo", ":example-kt-08ktor",
        ":example-jv-01reactiveui", ":example-jv-02threading", ":example-jv-03adapters",
        ":example-jv-04retrofit", ":example-jv-06db")

rootProject.name = "android-fore"
