package foo.bar.example.foreadapterskt.feature.playlist

import foo.bar.example.foreadapterskt.R
import java.util.Random


object RandomTrackGeneratorUtil {

    private val random = Random()

    private val colours = intArrayOf(R.color.pastel1, R.color.pastel2, R.color.pastel3, R.color.pastel4, R.color.pastel5)

    fun generateRandomColourResource(): Int {
        return randomInt(colours)
    }

    fun randomInt(intArray: IntArray): Int {
        return intArray[random.nextInt(intArray.size - 1)]
    }

}
