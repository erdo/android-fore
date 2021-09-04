package co.early.fore.kt.core.logging

import co.early.fore.core.utils.text.TextPadder


interface TagFormatter {
    fun limitTagLength(tag: String): String
    fun padTagWithSpace(tag: String): String
}

class TagFormatterImpl(private val overrideMaxTagLength: Int? = null) : TagFormatter {

    init {
        if (overrideMaxTagLength != null && overrideMaxTagLength<4){
            throw IllegalArgumentException("overrideMaxTagLength needs to be 4 or bigger, or not set at all")
        }
    }

    private val bookEndLength = (overrideMaxTagLength ?: MAX_TAG_LENGTH / 2) - 1
    private var longestTagLength = 0

    override fun limitTagLength(tag: String): String {
        return if (tag.length <= overrideMaxTagLength ?: MAX_TAG_LENGTH) {
            tag
        } else {
            tag.substring(0, bookEndLength) + ".." + tag.substring(tag.length - bookEndLength, tag.length)
        }
    }

    override fun padTagWithSpace(tag: String): String {
        longestTagLength = longestTagLength.coerceAtLeast(tag.length + 1)
        return if (longestTagLength != tag.length) {
            (TextPadder.padText(tag, longestTagLength, TextPadder.Pad.RIGHT, ' ') ?: tag)
        } else {
            tag
        }
    }

    companion object {
        private const val MAX_TAG_LENGTH = 30 //below API 24 is limited to 23 characters
    }
}
