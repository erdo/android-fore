package co.early.fore.core.utils.text;

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import co.early.fore.core.Affirm;

//TODO only using this for logging, simplify, but add tests, remove leftToRight
public class MonospacedTextWrappingUtils {

    private static final String LINE_BREAK = "\n";
    public static final String SPACE = " ";
    public static final String EMPTY = "";


    /**
     * Wrap fullText so that it will fit in charactersAvailable,
     * over however many lines it takes, suitable for monospaced fonts
     * <p>
     * Attempts to wrap on spaces between words if possible, if not will break mid-word
     * <p>
     * Honours the line break '\n'
     * <p>
     * Does not honour large spaces (made up of multiple space characters in a row) - all spaces between
     * words will return as single spaced
     *
     * @param fullText text that needs to be wrapped
     * @param charactersAvailable has to be larger than 0
     * @return lines of text, such that each line of text fits inside the charactersAvailable, any line
     * breaks in the original string will be converted to a empty string item in the list
     */
    public static List<String> wrapMonospaceText(String fullText, int charactersAvailable) {

        Affirm.notNull(fullText);

        if (charactersAvailable <= 0) {
            throw new IllegalArgumentException("charactersAvailable needs to be larger than 0, charactersAvailable:" + charactersAvailable);
        }

        boolean leftToRight = true; //TODO right to left languages, add tests and fix up (=false doesn't currently work)

        //separate any line breaks out
        List<String> linesWithExtractedLineBreaks = new ArrayList<>();
        expandLineBreaks(linesWithExtractedLineBreaks, fullText, leftToRight);

        List<String> finalLines = new ArrayList<>();

        for (String line : linesWithExtractedLineBreaks) {
            if (EMPTY.equals(line)) {
                finalLines.add(line);
            } else {
                String trimmedLine = line.trim();
                if (EMPTY.equals(trimmedLine)) {
                    finalLines.add(SPACE);
                } else {
                    //take the line with no line breaks in it and then wrap that
                    StringTokenizer stringTokenizer = new StringTokenizer(line, SPACE, false);
                    List<String> words = new ArrayList<String>();
                    while (stringTokenizer.hasMoreTokens()) {
                        words.add(stringTokenizer.nextToken());
                    }
                    extractWrappedLines(finalLines, words, -1, null, charactersAvailable, leftToRight);
                }
            }
        }
        return finalLines;
    }


    /**
     * Takes a string of text, replacing any '\n' line breaks with new lines which are added to the output list
     *
     * @param lines
     * @param lineWhichMayIncludeLineBreaks
     * @param leftToRight
     */

    private static void expandLineBreaks(List<String> lines, String lineWhichMayIncludeLineBreaks, boolean leftToRight) {

        int positionOfLineBreak = lineWhichMayIncludeLineBreaks.indexOf(LINE_BREAK);

        if (positionOfLineBreak < 0) { //no line breaks present
            lines.add((leftToRight ? lines.size() : 0), lineWhichMayIncludeLineBreaks);
        } else { //has a line break in the word
            lines.add((leftToRight ? lines.size() : 0), lineWhichMayIncludeLineBreaks.substring(0, positionOfLineBreak));
            expandLineBreaks(lines, lineWhichMayIncludeLineBreaks.substring(positionOfLineBreak + 1, lineWhichMayIncludeLineBreaks.length()), leftToRight);
        }
    }

    /**
     * Recursive method, takes a list of words and concatenates them in order and with spaces in between each word, up to a maximum line width of widthAvailable,
     * further lines are added to linesSoFar until all the wordsStillToWrap have been processed.
     *
     * @param linesSoFar
     * @param wordsStillToWrap
     * @param widthAvailable
     * @param paint            if null we will wrap based on number of charactersAvailable
     * @param leftToRight
     */
    private static void extractWrappedLines(List<String> linesSoFar, List<String> wordsStillToWrap, float widthAvailable, Paint paint, int charactersAvailable, boolean leftToRight) {

        if (wordsStillToWrap.size() > 0) {

            //start by assuming all the words still to wrap will wrap into one line
            String nextLineAttempt = flattenWordsAddingSpaces(wordsStillToWrap);

            int charactersThatFit;

            if (paint == null) {
                charactersThatFit = nextLineAttempt.length() < charactersAvailable ? nextLineAttempt.length() : charactersAvailable;
            } else {
                charactersThatFit = paint.breakText(nextLineAttempt, leftToRight, widthAvailable, null);
            }


            if (charactersThatFit == nextLineAttempt.length()) { //we're done

                linesSoFar.add(nextLineAttempt);
                wordsStillToWrap.clear();

            } else { //just get the next line, then pass the remaining words to the method again recursively

                List<String> wordsForNextLine = new ArrayList<String>();

                int charactersUsed = -1;//to take into account that there is no space before the first word in a line

                while (wordsStillToWrap.size() > 0 && charactersUsed + 1 + wordsStillToWrap.get(0).length() <= charactersThatFit) {

                    wordsForNextLine.add(wordsStillToWrap.get(0));
                    charactersUsed += (1 + wordsStillToWrap.get(0).length());
                    wordsStillToWrap.remove(0);
                }

                if (charactersUsed < 1) {//the next word must be very large so we need to take what we can off the front of it

                    String massiveWord = wordsStillToWrap.get(0);

                    if (charactersThatFit == 0) {//just in case
                        charactersThatFit = 1;
                    }

                    wordsForNextLine.add(massiveWord.substring(0, charactersThatFit));
                    wordsStillToWrap.remove(0);

                    if (massiveWord.length() > charactersThatFit) {
                        wordsStillToWrap.add(0, massiveWord.substring(charactersThatFit, massiveWord.length()));
                    }
                }

                linesSoFar.add(flattenWordsAddingSpaces(wordsForNextLine));

                extractWrappedLines(linesSoFar, wordsStillToWrap, widthAvailable, paint, charactersAvailable, leftToRight);

            }
        }
    }

    private static String flattenWordsAddingSpaces(List<String> words) {

        StringBuilder stringBuilder = new StringBuilder();

        for (String word : words) {
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }

        if (words.size() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

}
