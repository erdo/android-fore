package co.early.fore.core.utils.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class BasicTextWrapperTest {

    private static final String EMPTY_TEXT = "";
    private static final String SPACE_TEXT = " ";
    private static final String LONG_SPACE_TEXT = "     ";
    private static final String SHORT_TEXT = "hello";
    private static final String LONG_TEXT = "The quick brown fox jumped over the lazy dogs, the quick brown fox jumped over the lazy dogs.";
    private static final String LONG_TEXT_LONG_WORD = "The quick brown fox jumped over the lazy 1 abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789 the quick brown fox";

    private static final int ZERO_CHARACTERS_AVAILABLE = 0;
    private static final int TINY_CHARACTERS_AVAILABLE = 1;
    private static final int MEDIUM_CHARACTERS_AVAILABLE = 40;

    private static final String FIRST_LINE = "The quick brown fox jumped over the lazy";
    private static final String SECOND_LINE = "dogs, the quick brown fox jumped over";
    private static final String THIRD_LINE = "the lazy dogs.";

    private static final String DOUBLE_SPACE_TEXT = "The quick brown fox jumped over the    -multi space text";
    private static final String LINE_BREAK_TEXT = "The quick brown\nfox jumped over the abc defghi\njklmnopqrstuvwxyz0123\n\n\n45678 9abcde fghijklm nopqrs tuvwxyz0 123456";


    private static final String FIRST_LINE_DOUBLE_SPACE = "The quick brown fox jumped over the";
    private static final String FIRST_LINE_LINE_BREAK = "The quick brown";

    @Test
    public void testMediumText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(LONG_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(FIRST_LINE, wrappedLines.get(0));
        Assert.assertEquals(SECOND_LINE, wrappedLines.get(1));
        Assert.assertEquals(THIRD_LINE, wrappedLines.get(2));
        Assert.assertEquals(3, wrappedLines.size());
    }

    @Test
    public void testSmallText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(SHORT_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(SHORT_TEXT, wrappedLines.get(0));
        Assert.assertEquals(1, wrappedLines.size());
    }

    @Test
    public void testEmptyText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(EMPTY_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals("", wrappedLines.get(0));

        Assert.assertEquals(1, wrappedLines.size());
    }

    @Test
    public void testSpaceText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(SPACE_TEXT, wrappedLines.get(0));
        Assert.assertEquals(1, wrappedLines.size());
    }

    @Test
    public void testLongSpaceText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(LONG_SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(SPACE_TEXT, wrappedLines.get(0));//note we only expect one space back
        Assert.assertEquals(1, wrappedLines.size());
    }

    @Test
    public void testShortText_tinyWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(SHORT_TEXT, TINY_CHARACTERS_AVAILABLE);

        Assert.assertEquals("h", wrappedLines.get(0));
        Assert.assertEquals("e", wrappedLines.get(1));
        Assert.assertEquals("l", wrappedLines.get(2));
        Assert.assertEquals("l", wrappedLines.get(3));
        Assert.assertEquals("o", wrappedLines.get(4));

        Assert.assertEquals(5, wrappedLines.size());
    }

    @Test
    public void testLongTextWithLongWord_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(LONG_TEXT_LONG_WORD, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(FIRST_LINE, wrappedLines.get(0));
        Assert.assertEquals("1", wrappedLines.get(1));
        Assert.assertEquals("abcdefghijklmnopqrstuvwxyz0123456789abcd", wrappedLines.get(2));
        Assert.assertEquals("efghijklmnopqrstuvwxyz0123456789 the", wrappedLines.get(3));
        Assert.assertEquals("quick brown fox", wrappedLines.get(4));
        Assert.assertEquals(5, wrappedLines.size());
    }

    @Test
    public void testMultipleSpaceText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(DOUBLE_SPACE_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(FIRST_LINE_DOUBLE_SPACE, wrappedLines.get(0));
        Assert.assertEquals("-multi space text", wrappedLines.get(1));
        Assert.assertEquals(2, wrappedLines.size());
    }

    @Test
    public void testLineBreakText_mediumWidthAvailable() {

        List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(LINE_BREAK_TEXT, MEDIUM_CHARACTERS_AVAILABLE);

        Assert.assertEquals(FIRST_LINE_LINE_BREAK, wrappedLines.get(0));
        Assert.assertEquals("fox jumped over the abc defghi", wrappedLines.get(1));
        Assert.assertEquals("jklmnopqrstuvwxyz0123", wrappedLines.get(2));
        Assert.assertEquals("", wrappedLines.get(3));
        Assert.assertEquals("", wrappedLines.get(4));
        Assert.assertEquals("45678 9abcde fghijklm nopqrs tuvwxyz0", wrappedLines.get(5));
        Assert.assertEquals("123456", wrappedLines.get(6));

        Assert.assertEquals(7, wrappedLines.size());
    }


    @Test
    public void testLongText_zeroWidthAvailable() {

        boolean exceptionThrown = false;

        try {
            List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(LONG_TEXT, ZERO_CHARACTERS_AVAILABLE);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        Assert.assertEquals(true, exceptionThrown);
    }


}
