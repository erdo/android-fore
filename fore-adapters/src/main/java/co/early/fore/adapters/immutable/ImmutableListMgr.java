package co.early.fore.adapters.immutable;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import co.early.fore.adapters.Adaptable;
import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.threading.AsyncBuilder;
import co.early.fore.core.time.SystemTimeWrapper;

/**
 * This class holds the definition of the current list for an adapter, so it's getItemCount() and getItem()
 * functions are always correct.
 *
 * The way to change the list is to first call getListCopy(), make the changes to that list, and
 * then call updateList() which will return immediately.
 *
 * The DiffSpec between the old and the new list will be calculated off the UI thread and the
 * results applied atomically on the UI thread. Observers will be notified on the UI thread.
 *
 */
public class ImmutableListMgr<T extends DeepCopyable<T> & DiffComparator<T>>
        extends ObservableImp implements Diffable, Adaptable<T> {

    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;
    private DiffSpec latestDiffSpec;
    private List<T> currentList = new ArrayList<T>();
    private List<T> currentListMutableCopy = new ArrayList<T>();
    private int currentListVersion = 0;

    private final Object listChangesLock = new Object();

    public ImmutableListMgr(SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        this.workMode = workMode;
        latestDiffSpec = createFullDiffSpec();
    }

    public void changeList(ListUpdater<T> listUpdater){
        synchronized (listChangesLock) {
            listUpdater.updateList(currentListMutableCopy);
        }
        updateList(currentListMutableCopy);
    }

    public void replaceList(ListReplacer<T> listReplacer){
        updateList(listReplacer.replaceList());
    }

    @SuppressWarnings("unchecked")
    private void updateList(List<T> newList) {
        new AsyncBuilder<Input, Result>(workMode)
                .doInBackground(input -> doWork(input[0]))
                .onPostExecute(this::updateState)
                .execute(new Input(currentListVersion, currentList, newList));
    }

    @Override
    public T getItem(int index) {
        return currentList.get(index);
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    /**
     * If the DiffResult is old, then we assume that whatever changes
     * were made to the list last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the DiffResult and create a fresh one with a
     * full diff spec.
     *
     * @return the latest DiffResult for the list
     */
    @Override
    public DiffSpec getAndClearLatestDiffSpec(long maxAgeMs) {

        DiffSpec latestDiffSpecAvailable = latestDiffSpec;
        DiffSpec fullDiffSpec = createFullDiffSpec();

        latestDiffSpec = fullDiffSpec;

        if ((systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable.timeStamp) < maxAgeMs) {
            return latestDiffSpecAvailable;
        } else {
            return fullDiffSpec;
        }
    }

    private DiffSpec createFullDiffSpec() {
        return new DiffSpec(null, systemTimeWrapper);
    }

    private Result doWork(Input input) {

        List<T> newListCopy = new ArrayList<>(input.newList.size());
        List<T> newListCopy2 = new ArrayList<>(input.newList.size());

        synchronized (listChangesLock) {
            //create a mutable copy of the new list, ready for when client wants to change it
            for (T item : input.newList) {
                newListCopy.add(item.deepCopy());
                newListCopy2.add(item.deepCopy());
            }
        }

        // work out the differences in the lists
        DiffUtil.DiffResult diffResult = new DiffCalculator<T>().createDiffResult(input.oldList, newListCopy);

        //return to the UI thread
        return new Result(input.oldListVersion, newListCopy, newListCopy2, new DiffSpec(diffResult, systemTimeWrapper));
    }

    private void updateState(Result result) {

        if (result.oldListVersion != currentListVersion){
            return; // this is an old change, we ignore it
        }

        currentListVersion = currentListVersion + 1;
        currentList = result.newList;
        currentListMutableCopy = result.newListCopy;
        latestDiffSpec = result.diffSpec;

        notifyObservers();
    }

    private class Input {

        public final int oldListVersion;
        public final List<T> oldList;
        public final List<T> newList;

        public Input(int oldListVersion, List<T> oldList, List<T> newList) {
            this.oldListVersion = oldListVersion;
            this.oldList = Affirm.notNull(oldList);
            this.newList = Affirm.notNull(newList);
        }
    }

    private class Result {

        public final int oldListVersion;
        public final List<T> newList;
        public final List<T> newListCopy;
        public final DiffSpec diffSpec;

        public Result(int oldListVersion, List<T> newList, List<T> newListCopy, DiffSpec diffSpec) {
            this.oldListVersion = oldListVersion;
            this.newList = Affirm.notNull(newList);
            this.newListCopy = Affirm.notNull(newListCopy);
            this.diffSpec = Affirm.notNull(diffSpec);
        }
    }

    public interface ListUpdater<T>{
        void updateList(List<T> listCopy);
    }

    public interface ListReplacer<T>{
        List<T> replaceList();
    }
}
