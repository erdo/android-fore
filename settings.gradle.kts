include(":fore-core")
include(":fore-core-kt")
include(":fore-adapters")
include(":fore-adapters-kt")
include(":fore-network")
include(":fore-network-kt")
include(":fore-lifecycle")
include(":fore-jv")
include(":fore-kt")

include(":example-kt-01reactiveui", ":example-kt-02coroutine", "example-kt-03adapters",
        ":example-kt-04retrofit", ":example-kt-07apollo",
        ":example-jv-01reactiveui", ":example-jv-02threading", ":example-jv-03adapters",
        ":example-jv-04retrofit", ":example-jv-05ui", ":example-jv-06db")

rootProject.name = "android-fore"