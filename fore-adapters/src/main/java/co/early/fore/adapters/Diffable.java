package co.early.fore.adapters;

public interface Diffable {
    DiffSpec getAndClearLatestDiffSpec(long maxAgeMs);
}
