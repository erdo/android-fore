rootProject.name = "android-fore"

include(":fore-jv")
include(":fore-jv-core")
include(":fore-jv-android-core")
include(":fore-jv-android-adapters")
include(":fore-jv-android-network")

include(":fore-kt")
include(":fore-kt-core")
include(":fore-kt-android-core")
include(":fore-kt-android-adapters")
include(":fore-kt-android-network")

include(":example-kt-01reactiveui", ":example-kt-02coroutine", "example-kt-03adapters",
        ":example-kt-04retrofit", ":example-kt-07apollo", ":example-kt-07apollo3", ":example-kt-08ktor",
        ":example-jv-01reactiveui", ":example-jv-02threading", ":example-jv-03adapters",
        ":example-jv-04retrofit", ":example-jv-06db")

