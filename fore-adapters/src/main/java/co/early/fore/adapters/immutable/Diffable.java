package co.early.fore.adapters.immutable;

public interface Diffable {
    DiffSpec getAndClearLatestDiffSpec(long maxAgeMs);
}
