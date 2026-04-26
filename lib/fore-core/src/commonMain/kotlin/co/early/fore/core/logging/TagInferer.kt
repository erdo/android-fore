package co.early.fore.core.logging

interface TagInferer {
    fun inferTag(): String
}

expect fun getTagInferer(): TagInferer
