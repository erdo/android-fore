package co.early.fore.adapters;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

/**
 * By default Android will animate changes to an item in a list by cross-fading a copy of the view
 * before the item changed, to a version of the view after the change.
 *
 * This looks ok for simple changes, but if you want a more advanced animation (such as running a
 * lottie animation for example), the default cross-fade will interfere with that, so you can
 * turn it off by setting this class on the recycle view (see the sample apps)
 *
 */
public class CrossFadeRemover extends DefaultItemAnimator {

    @Override
    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> payloads) {
        return true;
    }

    @Override
    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        return true;
    }
}
