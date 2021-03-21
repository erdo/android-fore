package co.early.fore.adapters.immutable;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.threading.AsyncBuilder;
import co.early.fore.core.time.SystemTimeWrapper;

public class DiffableImp<T extends DeepCopyable<T> & DiffComparator<T>> extends ObservableImp implements Diffable {

    private static final String LOG_TAG = DiffableImp.class.getSimpleName();

    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;
    private final Logger logger;
    private DiffSpec latestDiffSpec = createFullDiffSpec();
    private List<T> currentList = new ArrayList<T>();
    private List<T> currentListMutableCopy = new ArrayList<T>();

    public DiffableImp(SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        this.workMode = workMode;
        this.logger = logger;
    }

    public T getItem(int index) {
        return currentList.get(index);
    }

    public List<T> getListCopy() {
        return currentListMutableCopy;
    }

    public int size() {
        return currentList.size();
    }

    @SuppressWarnings("unchecked")
    public void updateList(List<T> newList) {

        new AsyncBuilder<Input, Result>(workMode)
                .doInBackground(input -> doWork(input[0]))
                .onPostExecute(this::updateState)
                .execute(new Input(currentList, newList));
    }

    private Result doWork(Input input) {

        // work out the differences in the lists
        DiffUtil.DiffResult diffResult = new DiffCalculator<T>().createDiffResult(input.oldList, input.newList);

        //create a mutable copy of the new list, ready for when client wants to change it
        List<T> newListCopy = new ArrayList<>(input.newList.size());
        for (T item : input.newList) {
            newListCopy.add(item.deepCopy());
        }

        //return to the UI thread
        return new Result(input.newList, newListCopy, new DiffSpec(diffResult, systemTimeWrapper));
    }

    private void updateState(Result result) {
        currentList = result.newList;
        currentListMutableCopy = result.newListCopy;
        latestDiffSpec = result.diffSpec;
        logger.i(LOG_TAG, "list updated, thread:" + Thread.currentThread().getId());
        notifyObservers();
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

    private class Input {

        public final List<T> oldList;
        public final List<T> newList;

        public Input(List<T> oldList, List<T> newList) {
            this.oldList = Affirm.notNull(oldList);
            this.newList = Affirm.notNull(newList);
        }
    }

    private class Result {

        public final List<T> newList;
        public final List<T> newListCopy;
        public final DiffSpec diffSpec;

        public Result(List<T> newList, List<T> newListCopy, DiffSpec diffSpec) {
            this.newList = Affirm.notNull(newList);
            this.newListCopy = Affirm.notNull(newListCopy);
            this.diffSpec = Affirm.notNull(diffSpec);
        }
    }
}
