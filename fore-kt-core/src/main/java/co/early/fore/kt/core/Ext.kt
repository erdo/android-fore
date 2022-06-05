package co.early.fore.kt.core

@Deprecated("this isn't really related to a core fore feature, so will be removed in the next major release - you can always add it directly to your project")
fun String.nullIfBlank(): String? = if (this.isBlank()) null else this