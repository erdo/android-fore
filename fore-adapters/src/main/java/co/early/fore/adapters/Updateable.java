package co.early.fore.adapters;

public interface Updateable {
    UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs);
}
