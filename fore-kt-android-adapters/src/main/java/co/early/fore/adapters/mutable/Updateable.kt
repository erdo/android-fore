package co.early.fore.adapters.mutable

interface Updateable {
    fun getAndClearLatestUpdateSpec(maxAgeMs: Long): UpdateSpec
}
