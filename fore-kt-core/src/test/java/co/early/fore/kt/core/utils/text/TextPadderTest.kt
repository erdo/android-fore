package co.early.fore.kt.core.utils.text

import co.early.fore.core.utils.text.TextPadder
import co.early.fore.core.utils.text.TextPadder.padText
import org.junit.Assert
import org.junit.Test
import java.lang.Exception

class TextPadderTest {
    @Test
    fun testPaddingOnTheRight() {
        val paddedText = padText(TEXT_ORIG, 10, TextPadder.Pad.RIGHT, '+')
        Assert.assertEquals(TEXT_ORIG + "+++++++", paddedText)
    }

    @Test
    fun testPaddingOnTheLeft() {
        val paddedText = padText(TEXT_ORIG, 10, TextPadder.Pad.LEFT, '*')
        Assert.assertEquals("*******" + TEXT_ORIG, paddedText)
    }

    @Test
    fun testPaddingWhenAlreadyPadded() {
        val paddedText = padText(TEXT_ORIG, 3, TextPadder.Pad.LEFT, PADDING_CHARACTER)
        Assert.assertEquals(TEXT_ORIG, paddedText)
    }

    @Test
    fun testPaddingIllegalValues() {
        try {
            padText(TEXT_ORIG, 2, TextPadder.Pad.LEFT, PADDING_CHARACTER)
            Assert.fail("should have thrown an exception by now: original text was already longer than the desiredLength")
        } catch (e: Exception) {
        }
    }

    companion object {
        private const val TEXT_ORIG = "foo"
        private const val PADDING_CHARACTER = '+'
    }
}