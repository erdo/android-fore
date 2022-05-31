package co.early.fore.adapters.immutable;

import androidx.recyclerview.widget.DiffUtil;

public interface DiffComparator<T> {

    /**
     * Used by {@link DiffUtil}
     *
     * Do the two instances represent the same real world item? even though they maybe
     * different instances. For example, one could be its representation in a list view, the other
     * could be its representation in a database entity, but if they represent the same conceptual
     * item then this method should return true.
     *
     * You might use some unique itemId for this, maybe an ISBN number if we are dealing with books,
     * maybe a title and year of release for a movie (providing you can guarantee this combination
     * is unique) - you certainly wouldn't use the item's position in a list for example.
     *
     * Note: even if the item's states are different - e.g. the same move, but one item has this movie
     * with a 4 star rating, the other item is the same movie but with a 3 star rating, they are
     * still the same conceptual item, just with different states, so this method should return true.
     *
     * @param other the instance to compare this with
     * @return true if the items represent the same real world / conceptual item
     */
    boolean itemsTheSame(T other);

    /**
     * Used by {@link DiffUtil}
     *
     * Note this really means do they look the same in a specific list on the display. As such
     * there can be multiple implementations, each related to a particular list display.
     *
     * Let's say we are displaying a list of stocks and each stock item in the list has: a ticker
     * symbol, a stock price, and a "social score" based on twitter mentions in the past hour.
     *
     * For our specific list in the UI, we only display the ticker symbol and the stock price. This
     * means that the social score does not effect the display in any way. If the social score is
     * the only thing to change, but the ticker symbol and the stock price remain the same, then
     * the new state of the item will look the same when viewed in this list - and we would return
     * true here (even though the item itself is no longer in the same state when considered as a
     * whole). It's important to get this right, it's how android adapters know whether to run an
     * update animation or not (by default android briefly by fades in and out the item in the view).
     *
     * @param other the instance to compare this with
     * @return true if the items will look the same when displayed
     */
    boolean itemsLookTheSame(T other);
}
