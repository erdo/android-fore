package co.early.fore.lifecycle.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;
import co.early.fore.core.observer.Observable;
import co.early.fore.core.ui.SyncableView;
import co.early.fore.lifecycle.LifecycleSyncer;

/**
 * <p>
 *      Convenience class that uses a {@link LifecycleSyncer} instance to ensure that
 *      {@link SyncableView#syncView()} is called whenever the relevant Observable models change.
 *      Also uses android lifecycle hooks to tell {@link LifecycleSyncer} when to add and remove
 *      observers to prevent memory leaks.</p>
 *
 * <p>
 *      If your app architecture uses custom views, to add fore behaviour to your custom view
 *      instead of extending CardView, extend this class instead.
 * </p>
 *
 * <p>
 * To use this class, you need to:
 * </p>
 * <ul>
 *      <li>Extend it</li>
 *      <li>Implement {@link SyncableView#syncView()} </li>
 *      <li>Implement {@link #getThingsToObserve()} by returning a {@link LifecycleSyncer.Observables}
 *      instance constructed with all the {@link Observable} models that the view is interested in</li>
 *      <li>If you override onFinishInflate() in your own class, you must call super.onFinishInflate()</li>
 * </ul>
 *
 */
public abstract class SyncCardView extends CardView implements SyncableView {

    private LifecycleSyncer lifecycleSyncer;

    public SyncCardView(Context context) {
        super(context);
    }

    public SyncCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SyncCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        lifecycleSyncer = new LifecycleSyncer(this, getThingsToObserve());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (lifecycleSyncer == null){
            throw new RuntimeException("You must call super.onFinishInflate() from within your onFinishInflate() method");
        }
        // add our observer to any models we want to observe
        lifecycleSyncer.addObserversAndSync();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // remove our observer from any models we are observing
        lifecycleSyncer.removeObservers();
    }

    public abstract LifecycleSyncer.Observables getThingsToObserve();

}
