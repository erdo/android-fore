package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.kt.net.retrofit2.Retrofit2CallProcessor
import co.early.fore.kt.Either
import co.early.fore.net.retrofit2.MessageProvider
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.message.UserMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response

/**
 *
 */
class StateBuilder internal constructor(private val mockRetrofit2CallProcessor: Retrofit2CallProcessor<UserMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockRetrofit2CallProcessor.processCallAsync(
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns CompletableDeferred(Either.right(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(userMessage: UserMessage): StateBuilder {

        coEvery {
            mockRetrofit2CallProcessor.processCallAwait(
                any() as Class<MessageProvider<UserMessage>>,
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns Either.left(userMessage)

        return this
    }
}
