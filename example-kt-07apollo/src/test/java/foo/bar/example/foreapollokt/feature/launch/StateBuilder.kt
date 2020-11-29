package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.kt.apollo.CallProcessor
import co.early.fore.kt.apollo.Either
import co.early.fore.apollo.MessageProvider
import foo.bar.example.foreapollokt.api.fruits.Launch
import foo.bar.example.foreapollokt.message.UserMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response

/**
 *
 */
class StateBuilder internal constructor(private val mockCallProcessor: CallProcessor<UserMessage>) {

    internal fun getFruitSuccess(launch: Launch): StateBuilder {

        coEvery {
            mockCallProcessor.processCallAsync(
                any() as suspend () -> Response<List<Launch>>
            )
        } returns CompletableDeferred(Either.right(listOf(launch)))

        return this
    }

    internal fun getFruitFail(userMessage: UserMessage): StateBuilder {

        coEvery {
            mockCallProcessor.processCallAwait(
                any() as Class<MessageProvider<UserMessage>>,
                any() as suspend () -> Response<List<Launch>>
            )
        } returns Either.left(userMessage)

        return this
    }
}
