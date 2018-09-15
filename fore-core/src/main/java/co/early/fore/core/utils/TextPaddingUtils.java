package co.early.fore.core.utils;

import co.early.fore.core.Affirm;

public class TextPaddingUtils {

    public enum Padding {
        START,
        END;
    }

    public static String padText(String textNeedingPadding, int lengthOfTargetText, Padding padding, char paddingCharacter) {

        Affirm.notNull(textNeedingPadding);
        Affirm.notNull(padding);

        int paddingCharactersRequired = lengthOfTargetText - textNeedingPadding.length();

        if (paddingCharactersRequired < 0) {
            throw new IllegalArgumentException("textNeedingPadding is already longer than the lengthOfTargetText, textNeedingPadding.length():" + textNeedingPadding.length() + " lengthOfTargetText" + lengthOfTargetText);
        }

        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < paddingCharactersRequired; ii++) {
            sb.append(paddingCharacter);
        }

        if (padding == Padding.START) {
            sb.append(textNeedingPadding);
        } else {
            sb.insert(0, textNeedingPadding);
        }

        return sb.toString();
    }

}
