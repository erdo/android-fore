package foo.bar.example.foreadapterskt.ui

import android.view.View

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

import androidx.recyclerview.widget.RecyclerView

/**
 *
 * https://stackoverflow.com/questions/30361068/assert-proper-number-of-items-in-list-with-espresso
 *
 */
object EspressoTestMatchers {

    fun withRecyclerViewItems(size: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            public override fun matchesSafely(view: View): Boolean {
                return (view as RecyclerView).adapter!!.itemCount == size
            }

            override fun describeTo(description: Description) {
                description.appendText("RecycleView should have $size items")
            }
        }
    }
}
