package foo.bar.example.foreapollokt.feature.fruit

import co.early.fore.kt.retrofit.CallProcessor
import co.early.fore.kt.retrofit.Either
import co.early.fore.retrofit.MessageProvider
import foo.bar.example.foreapollokt.api.fruits.FruitPojo
import foo.bar.example.foreapollokt.message.UserMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response

/**
 *
 */
class StateBuilder internal constructor(private val mockCallProcessor: CallProcessor<UserMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockCallProcessor.processCallAsync(
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns CompletableDeferred(Either.right(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(userMessage: UserMessage): StateBuilder {

        coEvery {
            mockCallProcessor.processCallAwait(
                any() as Class<MessageProvider<UserMessage>>,
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns Either.left(userMessage)

        return this
    }
}
