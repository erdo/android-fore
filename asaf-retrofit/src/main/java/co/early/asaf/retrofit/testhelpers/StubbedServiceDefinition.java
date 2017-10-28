package co.early.asaf.retrofit.testhelpers;

import java.io.IOException;

import co.early.asaf.core.Affirm;

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
        this.ioException = Affirm.notNull(ioException);
        this.expectedResult = Affirm.notNull(expectedResult);
    }

    public StubbedServiceDefinition(int httpCode, String resourceFileName, R expectedResult) {
        this(httpCode, resourceFileName, "application/json", expectedResult);
    }

    public StubbedServiceDefinition(int httpCode, String resourceFileName, String mimeType, R expectedResult) {
        this.httpCode = httpCode;
        this.resourceFileName = Affirm.notNull(resourceFileName);
        this.mimeType = Affirm.notNull(mimeType);
        this.ioException = null;
        this.expectedResult = Affirm.notNull(expectedResult);
    }

    public boolean successfullyConnected(){
        return ioException == null;
    }
}
