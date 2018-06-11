package co.early.asaf.adapters;

public interface Diffable {
    DiffSpec getAndClearLatestDiffSpec(long maxAgeMs);
}
