package co.early.fore.core.logging

actual fun getTagInferer(): TagInferer = MingwTagInferer()

class MingwTagInferer : TagInferer {

    override fun inferTag(): String {
        return "MinGW (Unknown Tag)"
    }
}