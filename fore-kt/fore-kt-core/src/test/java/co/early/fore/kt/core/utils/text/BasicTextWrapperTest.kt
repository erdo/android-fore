package co.early.fore.kt.core.utils.text

import co.early.fore.core.utils.text.BasicTextWrapper.wrapMonospaceText
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException

class BasicTextWrapperTest {
    @Test
    fun testMediumText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(LONG_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(FIRST_LINE, wrappedLines[0])
        Assert.assertEquals(SECOND_LINE, wrappedLines[1])
        Assert.assertEquals(THIRD_LINE, wrappedLines[2])
        Assert.assertEquals(3, wrappedLines.size.toLong())
    }

    @Test
    fun testSmallText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(SHORT_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(SHORT_TEXT, wrappedLines[0])
        Assert.assertEquals(1, wrappedLines.size.toLong())
    }

    @Test
    fun testEmptyText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(EMPTY_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals("", wrappedLines[0])
        Assert.assertEquals(1, wrappedLines.size.toLong())
    }

    @Test
    fun testSpaceText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(SPACE_TEXT, wrappedLines[0])
        Assert.assertEquals(1, wrappedLines.size.toLong())
    }

    @Test
    fun testLongSpaceText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(LONG_SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(SPACE_TEXT, wrappedLines[0]) //note we only expect one space back
        Assert.assertEquals(1, wrappedLines.size.toLong())
    }

    @Test
    fun testShortText_tinyWidthAvailable() {
        val wrappedLines = wrapMonospaceText(SHORT_TEXT, TINY_CHARACTERS_AVAILABLE)
        Assert.assertEquals("h", wrappedLines[0])
        Assert.assertEquals("e", wrappedLines[1])
        Assert.assertEquals("l", wrappedLines[2])
        Assert.assertEquals("l", wrappedLines[3])
        Assert.assertEquals("o", wrappedLines[4])
        Assert.assertEquals(5, wrappedLines.size.toLong())
    }

    @Test
    fun testLongTextWithLongWord_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(LONG_TEXT_LONG_WORD, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(FIRST_LINE, wrappedLines[0])
        Assert.assertEquals("1", wrappedLines[1])
        Assert.assertEquals("abcdefghijklmnopqrstuvwxyz0123456789abcd", wrappedLines[2])
        Assert.assertEquals("efghijklmnopqrstuvwxyz0123456789 the", wrappedLines[3])
        Assert.assertEquals("quick brown fox", wrappedLines[4])
        Assert.assertEquals(5, wrappedLines.size.toLong())
    }

    @Test
    fun testMultipleSpaceText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(DOUBLE_SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(FIRST_LINE_DOUBLE_SPACE, wrappedLines[0])
        Assert.assertEquals("-multi space text", wrappedLines[1])
        Assert.assertEquals(2, wrappedLines.size.toLong())
    }
    
    @Test
    fun testLineBreakText_mediumWidthAvailable() {
        val wrappedLines = wrapMonospaceText(LINE_BREAK_TEXT, MEDIUM_CHARACTERS_AVAILABLE)
        Assert.assertEquals(FIRST_LINE_LINE_BREAK, wrappedLines[0])
        Assert.assertEquals("fox jumped over the abc defghi", wrappedLines[1])
        Assert.assertEquals("jklmnopqrstuvwxyz0123", wrappedLines[2])
        Assert.assertEquals("", wrappedLines[3])
        Assert.assertEquals("", wrappedLines[4])
        Assert.assertEquals("45678 9abcde fghijklm nopqrs tuvwxyz0", wrappedLines[5])
        Assert.assertEquals("123456", wrappedLines[6])
        Assert.assertEquals(7, wrappedLines.size.toLong())
    }

    @Test
    fun testLongText_zeroWidthAvailable() {
        var exceptionThrown = false
        try {
            val wrappedLines = wrapMonospaceText(LONG_TEXT, ZERO_CHARACTERS_AVAILABLE)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
        }
        Assert.assertTrue(exceptionThrown)
    }

    companion object {
        private const val EMPTY_TEXT = ""
        private const val SPACE_TEXT = " "
        private const val LONG_SPACE_TEXT = "     "
        private const val SHORT_TEXT = "hello"
        private const val LONG_TEXT =
            "The quick brown fox jumped over the lazy dogs, the quick brown fox jumped over the lazy dogs."
        private const val LONG_TEXT_WITH_QUOTES =
            "{\"value\":\"we don't want this very long json string value to be wrapped with a line break somewhere in the middle because when we copy paste it, we won't be able to reformat it to json properly if it includes the line break\"}"
        private const val LONG_TEXT_LONG_WORD =
            "The quick brown fox jumped over the lazy 1 abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789 the quick brown fox"
        private const val ZERO_CHARACTERS_AVAILABLE = 0
        private const val TINY_CHARACTERS_AVAILABLE = 1
        private const val MEDIUM_CHARACTERS_AVAILABLE = 40
        private const val FIRST_LINE = "The quick brown fox jumped over the lazy"
        private const val SECOND_LINE = "dogs, the quick brown fox jumped over"
        private const val THIRD_LINE = "the lazy dogs."
        private const val DOUBLE_SPACE_TEXT =
            "The quick brown fox jumped over the    -multi space text"
        private const val LINE_BREAK_TEXT =
            "The quick brown\nfox jumped over the abc defghi\njklmnopqrstuvwxyz0123\n\n\n45678 9abcde fghijklm nopqrs tuvwxyz0 123456"
        private const val FIRST_LINE_DOUBLE_SPACE = "The quick brown fox jumped over the"
        private const val FIRST_LINE_LINE_BREAK = "The quick brown"
    }
}