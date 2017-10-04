package co.early.asaf.framework.threading;

/**
 *
 */
public interface PublishProgressCallback<T> {
    void publishProgress(T... payload);
}
