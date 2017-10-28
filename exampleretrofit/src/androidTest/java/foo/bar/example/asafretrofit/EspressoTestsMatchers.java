package foo.bar.example.asafretrofit;

import android.view.View;

import org.hamcrest.Matcher;

/**
 *
 * https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
 * https://github.com/dbottillo/Blog/blob/espresso_match_imageview/app/src/androidTest/java/com/danielebottillo/blog/config/EspressoTestsMatchers.java
 *
 */
public class EspressoTestsMatchers {

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(DrawableMatcher.EMPTY);
    }

    public static Matcher<View> hasDrawable() {
        return new DrawableMatcher(DrawableMatcher.ANY);
    }
}