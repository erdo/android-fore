package co.early.asaf.framework.callbacks;

/**
 *
 */
public interface DoThisWithVarargPayloadCallback<T> {
    void doThis(T... payload);
}
