package co.early.fore.kt.core

fun String.nullIfBlank(): String? = if (this.isBlank()) null else this