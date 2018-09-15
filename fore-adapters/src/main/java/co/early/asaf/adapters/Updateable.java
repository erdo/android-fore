package co.early.asaf.adapters;

public interface Updateable {
    UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs);
}
