package co.early.asaf.retrofit;

import retrofit2.Response;

/**
 *
 */
public interface ErrorHandler<M> {
    <CE extends MessageProvider<M>> M handleError(Throwable t, Response errorResponse, Class<CE> customErrorClazz);
}
