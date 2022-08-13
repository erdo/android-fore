package co.early.fore.adapters

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * By default Android will animate changes to an item in a list by cross-fading a copy of the view
 * before the item changed, to a version of the view after the change.
 *
 * This looks ok for simple changes, but if you want a more advanced animation (such as running a
 * lottie animation for example), the default cross-fade will interfere with that, so you can
 * turn it off by setting this class on the recycle view (see the sample apps)
 *
 */
class CrossFadeRemover : DefaultItemAnimator() {
    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ): Boolean {
        return true
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }
}
