package foo.bar.example.foreretrofit.feature.fruit;

import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.net.retrofit2.CallProcessorRetrofit2;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.message.UserMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 *
 */
public class StateBuilder {

    private CallProcessorRetrofit2<UserMessage> mockCallProcessor;

    StateBuilder(CallProcessorRetrofit2<UserMessage> mockCallProcessor) {
        this.mockCallProcessor = mockCallProcessor;
    }

    StateBuilder getFruitSuccess(final FruitPojo fruitPojo) {

        List<FruitPojo> fruitList = new ArrayList<>();
        fruitList.add(fruitPojo);

        final ArgumentCaptor<SuccessCallbackWithPayload> callback = ArgumentCaptor.forClass(SuccessCallbackWithPayload.class);

        doAnswer(__ -> {
            callback.getValue().success(fruitList);
            return null;
        })

                .when(mockCallProcessor)
                .processCall(any(), any(), any(), callback.capture(), any());

        return this;
    }

    StateBuilder getFruitFail(final UserMessage userMessage) {

        final ArgumentCaptor<FailureCallbackWithPayload> callback = ArgumentCaptor.forClass(FailureCallbackWithPayload.class);

        doAnswer(__ -> {
            callback.getValue().fail(userMessage);
            return null;
        })
        .when(mockCallProcessor)
        .processCall(any(), any(), any(), any(), callback.capture());

        return this;
    }

}
