package co.early.fore.core.logging

actual fun getTagInferer(): TagInferer = TagInfererLinux()

class TagInfererLinux : TagInferer {

    override fun inferTag(): String {
        return "Linux (Unknown Tag)"
    }
}
