package co.early.fore.net.testhelpers;

import java.io.IOException;

/**
 *
 * @param <R> The class type expected to be passed back as the result
 */
public class StubbedServiceDefinition<R> {

    public final int httpCode;
    public final String resourceFileName;
    public final String mimeType;

    public final IOException ioException;

    public final R expectedResult;

    public StubbedServiceDefinition(IOException ioException, R expectedResult) {
        this.httpCode = 0;
        this.resourceFileName = null;
        this.mimeType = null;
        this.ioException = notNull(ioException);
        this.expectedResult = expectedResult;
    }

    public StubbedServiceDefinition(int httpCode, String resourceFileName) {
        this(httpCode, resourceFileName, "application/json", null);
    }

    public StubbedServiceDefinition(int httpCode, String resourceFileName, R expectedResult) {
        this(httpCode, resourceFileName, "application/json", expectedResult);
    }

    public StubbedServiceDefinition(int httpCode, String resourceFileName, String mimeType, R expectedResult) {
        this.httpCode = httpCode;
        this.resourceFileName = notNull(resourceFileName);
        this.mimeType = notNull(mimeType);
        this.ioException = null;
        this.expectedResult = expectedResult;
    }

    public boolean successfullyConnected(){
        return ioException == null;
    }

    private <T> T notNull(T param) {
        if (param == null) {
            throw new NullPointerException("Parameter must not be null");
        }
        return param;
    }
}
