package foo.bar.example.foreadapters.feature.playlist;

import java.util.Random;

import foo.bar.example.foreadapters.R;

public class RandomTrackGeneratorUtil {

    private static Random random = new Random();

    private static int[] colours = {
            R.color.pastel1,
            R.color.pastel2,
            R.color.pastel3,
            R.color.pastel4,
            R.color.pastel5
    };

    public static int generateRandomColourResource() {
        return randomInt(colours);
    }

    public static int randomInt(int[] intArray) {
        return intArray[random.nextInt(intArray.length - 1)];
    }
}
