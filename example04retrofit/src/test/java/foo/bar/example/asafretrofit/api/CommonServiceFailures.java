package foo.bar.example.asafretrofit.api;

import java.io.IOException;
import java.util.ArrayList;

import co.early.fore.retrofit.testhelpers.StubbedServiceDefinition;
import foo.bar.example.asafretrofit.message.UserMessage;

/**
 * This will be specific to your own app, it's closely linked with: @{@link CustomGlobalErrorHandler}
 */
public class CommonServiceFailures extends ArrayList<StubbedServiceDefinition<UserMessage>>{


    public CommonServiceFailures() {

        //network down
        add(new StubbedServiceDefinition(new IOException("fake io exception for testing purposes"), UserMessage.ERROR_NETWORK));

        //bad request
        add(new StubbedServiceDefinition(400, "common/empty.json", UserMessage.ERROR_CLIENT));

        //session timeout
        add(new StubbedServiceDefinition(401, "common/empty.json", UserMessage.ERROR_SESSION_TIMED_OUT));

        //missing resource
        add(new StubbedServiceDefinition(404, "common/empty.json", UserMessage.ERROR_SERVER));//realise this is officially a "client" error, but in our experience this is usually the fault of the server

        //missing resource (html page)
        add(new StubbedServiceDefinition(404, "common/html.json", "text/html", UserMessage.ERROR_SERVER));

        //bad request
        add(new StubbedServiceDefinition(405, "common/empty.json", UserMessage.ERROR_CLIENT));

        //server down
        add(new StubbedServiceDefinition(500, "common/empty.json", UserMessage.ERROR_SERVER));

        //service unavailable
        add(new StubbedServiceDefinition(503, "common/empty.json", UserMessage.ERROR_SERVER));

        //non valid json
        add(new StubbedServiceDefinition(200, "common/html.json", UserMessage.ERROR_SERVER));

    }

}
