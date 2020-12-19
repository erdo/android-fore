package co.early.fore.core.ui;

public interface AutoSyncable {
    void addObserversAndSync(SyncableView view);
    void removeObservers();
}
