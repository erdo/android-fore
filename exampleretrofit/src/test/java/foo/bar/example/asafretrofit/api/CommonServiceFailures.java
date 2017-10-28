package foo.bar.example.asafretrofit.api;

import java.io.IOException;
import java.util.ArrayList;

import co.early.asaf.retrofit.testhelpers.StubbedServiceDefinition;
import foo.bar.example.asafretrofit.message.UserMessage;

/**
 * This will be specific to your own app, it's closely linked with: @{@link CustomGlobalErrorHandler}
 */
public class CommonServiceFailures extends ArrayList<StubbedServiceDefinition<UserMessage>>{


    public CommonServiceFailures() {

        //network down
        add(new StubbedServiceDefinition(new IOException("fake io exception for testing purposes"), UserMessage.ERROR_NETWORK));

        //bad request
        add(new StubbedServiceDefinition(400, "", UserMessage.ERROR_CLIENT));

        //session timeout
        add(new StubbedServiceDefinition(401, "", UserMessage.ERROR_SESSION_TIMED_OUT));

        //missing resource
        add(new StubbedServiceDefinition(404, "", UserMessage.ERROR_SERVER));//realise this is officially a "client" error, but in our experience this is usually the fault of the server

        //missing resource (html page)
        add(new StubbedServiceDefinition(404, "<HTML>/<HTML>","text/html", UserMessage.ERROR_SERVER));

        //bad request
        add(new StubbedServiceDefinition(405, "", UserMessage.ERROR_CLIENT));

        //server down
        add(new StubbedServiceDefinition(500, "", UserMessage.ERROR_SERVER));

        //service unavailable
        add(new StubbedServiceDefinition(503, "", UserMessage.ERROR_SERVER));

        //non valid json
        add(new StubbedServiceDefinition(200, "<HTML>/<HTML>", UserMessage.ERROR_SERVER));

    }

}
