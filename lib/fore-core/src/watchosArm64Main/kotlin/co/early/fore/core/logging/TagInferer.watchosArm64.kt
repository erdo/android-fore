package co.early.fore.core.logging

import platform.Foundation.NSThread

actual fun getTagInferer(): TagInferer = TagInfererWatchosArm64()

class TagInfererWatchosArm64 : TagInferer {

    override fun inferTag(): String {
        // Fetch the current thread's call stack symbols
        val stackSymbols = NSThread.callStackSymbols

        return if (stackSymbols.size > 2) {
            extractClassName(stackSymbols[2].toString()).let {
                if ((it == "ObservableImp" || it == "Fore\$Companion") && stackSymbols.size > 3) {
                    extractClassName(stackSymbols[3].toString())
                } else {
                    it
                }
            }
        } else "missing stacktrace"
    }

    private fun extractClassName(symbol: String): String {
        // Thread.callStackSymbols gives symbols with module name, memory address, and function signature
        // We can try to parse it to extract the meaningful class/function name
        // Example symbol: 0   MyApp                         0x00000001022ecac8 main + 32
        return symbol.substringAfter(" ").substringBefore(" ")
    }
}
