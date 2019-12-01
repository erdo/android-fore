package foo.bar.example.foreretrofitcoroutine.feature.fruit

import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.retrofit.MessageProvider
import co.early.fore.retrofit.coroutine.CallProcessor
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import io.mockk.every
import io.mockk.slot

/**
 *
 */
class StateBuilder internal constructor(private val mockCallProcessor: CallProcessor<UserMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        val slot = slot<co.early.fore.core.callbacks.SuccessWithPayload<List<FruitPojo>>>()

        every {
            mockCallProcessor.processCall(any(), any() as Class<MessageProvider<UserMessage>>, capture(slot), any(), any())
        } answers {
            slot.captured(listOf(fruitPojo))
        }

        return this
    }

    internal fun getFruitFail(userMessage: UserMessage): StateBuilder {

        val slot = slot<co.early.fore.core.callbacks.FailureWithPayload<UserMessage>>()

        every {
            mockCallProcessor.processCall(any(), any() as Class<MessageProvider<UserMessage>>, any<co.early.fore.core.callbacks.SuccessWithPayload<List<FruitPojo>>>(), capture(slot), any())
        } answers {
            slot.captured.invoke(userMessage)
        }

        return this
    }
}
