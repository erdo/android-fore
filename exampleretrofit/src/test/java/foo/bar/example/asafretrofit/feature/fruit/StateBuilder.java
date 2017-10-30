package foo.bar.example.asafretrofit.feature.fruit;

import org.mockito.ArgumentCaptor;

import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallbackWithPayload;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.message.UserMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 *
 */
public class StateBuilder {

    private CallProcessor<UserMessage> mockCallProcessor;

    StateBuilder(CallProcessor<UserMessage> mockCallProcessor) {
        this.mockCallProcessor = mockCallProcessor;
    }

    StateBuilder getFruitSuccess(final FruitPojo fruitPojo) {

        final ArgumentCaptor<SuccessCallbackWithPayload> callback = ArgumentCaptor.forClass(SuccessCallbackWithPayload.class);

        doAnswer(__ -> {
            callback.getValue().success(fruitPojo);
            return null;
        })

                .when(mockCallProcessor)
                .processCall(any(), any(), callback.capture(), any());

        return this;
    }

    StateBuilder getFruitFail(final UserMessage userMessage) {

        final ArgumentCaptor<FailureCallbackWithPayload> callback = ArgumentCaptor.forClass(FailureCallbackWithPayload.class);

        doAnswer(__ -> {
            callback.getValue().fail(userMessage);
            return null;
        })

                .when(mockCallProcessor)
                .processCall(any(), any(), any(), callback.capture());

        return this;
    }

}
