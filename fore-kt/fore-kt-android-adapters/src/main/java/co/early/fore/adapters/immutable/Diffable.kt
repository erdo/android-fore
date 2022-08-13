package co.early.fore.adapters.immutable

interface Diffable {
    fun getAndClearLatestDiffSpec(maxAgeMs: Long): DiffSpec
}