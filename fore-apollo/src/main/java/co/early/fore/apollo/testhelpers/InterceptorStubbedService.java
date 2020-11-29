package co.early.fore.apollo.testhelpers;

import java.io.IOException;

import co.early.fore.core.Affirm;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Using this interceptor will simulate a canned server response at the okhttp level
 * without actually connecting to a real server
 *
 * This interceptor will easily work for models that are only calling one endpoint,
 * for more complex models with multiple end points returning different pojos
 * you would need slightly more sophistication here to intercept each network
 * request in a different way (you may also be able to construct the model under test
 * with a new Interceptor setup for each test).
 *
 */
public class InterceptorStubbedService implements Interceptor {

    private final StubbedServiceDefinition stubbedServiceDefinition;

    public InterceptorStubbedService(StubbedServiceDefinition stubbedServiceDefinition) {
        this.stubbedServiceDefinition = Affirm.notNull(stubbedServiceDefinition);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        if (!stubbedServiceDefinition.successfullyConnected()) { //stubbing a connection failure
            throw stubbedServiceDefinition.ioException;
        }

        String bodyString = JunitFileIO.getRawResourceAsUTF8String(stubbedServiceDefinition.resourceFileName, this.getClass().getClassLoader());

        return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(stubbedServiceDefinition.httpCode)
                    .body(ResponseBody.create(MediaType.parse(stubbedServiceDefinition.mimeType), bodyString))
                    .message("")
                    .build();
    }

}

