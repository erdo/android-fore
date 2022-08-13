package co.early.fore.core.utils.text

import java.lang.NullPointerException
import java.lang.StringBuilder

object TextPadder {
    fun padText(
        textOriginal: String,
        desiredLength: Int,
        pad: Pad,
        paddingCharacter: Char
    ): String {
        notNull(textOriginal)
        notNull(pad)
        val paddingCharactersRequired = desiredLength - textOriginal.length
        require(paddingCharactersRequired >= 0) { "textOriginal is already longer than the desiredLength, textOriginal.length():" + textOriginal.length + " desiredLength:" + desiredLength }
        val sb = StringBuilder()
        for (ii in 0 until paddingCharactersRequired) {
            sb.append(paddingCharacter)
        }
        if (pad == Pad.LEFT) {
            sb.append(textOriginal)
        } else {
            sb.insert(0, textOriginal)
        }
        return sb.toString()
    }

    private fun <T> notNull(param: T?): T {
        if (param == null) {
            throw NullPointerException("Parameter must not be null")
        }
        return param
    }

    enum class Pad {
        LEFT, RIGHT
    }
}