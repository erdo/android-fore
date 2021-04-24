package co.early.fore.adapters.mutable;

public interface Updateable {
    UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs);
}
