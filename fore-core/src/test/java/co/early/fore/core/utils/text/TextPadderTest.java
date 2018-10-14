package co.early.fore.core.utils.text;

import org.junit.Assert;
import org.junit.Test;


public class TextPadderTest {

    private static final String TEXT_ORIG = "foo";
    private static final char PADDING_CHARACTER = '+';

    @Test
    public void testPaddingOnTheRight() {

        String paddedText = TextPadder.padText(TEXT_ORIG, 10, TextPadder.Pad.RIGHT, '+');

        Assert.assertEquals(TEXT_ORIG + "+++++++", paddedText);
    }


    @Test
    public void testPaddingOnTheLeft() {

        String paddedText = TextPadder.padText(TEXT_ORIG, 10, TextPadder.Pad.LEFT, '*');

        Assert.assertEquals("*******" + TEXT_ORIG, paddedText);
    }


    @Test
    public void testPaddingWhenAlreadyPadded() {

        String paddedText = TextPadder.padText(TEXT_ORIG, 3, TextPadder.Pad.LEFT, PADDING_CHARACTER);

        Assert.assertEquals(TEXT_ORIG, paddedText);
    }


    @Test
    public void testPaddingIllegalValues() {

        try {
            TextPadder.padText(null, 3, TextPadder.Pad.LEFT, PADDING_CHARACTER);
            Assert.fail("should have thrown an exception by now: original text was null");
        } catch (Exception e) {
        }

        try {
            TextPadder.padText(TEXT_ORIG, 3, null, PADDING_CHARACTER);
            Assert.fail("should have thrown an exception by now: pad direction was null");
        } catch (Exception e) {
        }

        try {
            TextPadder.padText(TEXT_ORIG, 2, TextPadder.Pad.LEFT, PADDING_CHARACTER);
            Assert.fail("should have thrown an exception by now: original text was already longer than the desiredLength");
        } catch (Exception e) {
        }
    }

}
