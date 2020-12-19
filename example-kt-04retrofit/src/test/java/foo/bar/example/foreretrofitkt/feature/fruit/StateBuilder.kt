package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.kt.net.retrofit2.CallProcessorRetrofit2
import co.early.fore.kt.core.Either
import co.early.fore.net.retrofit2.MessageProvider
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response

/**
 *
 */
class StateBuilder internal constructor(private val mockCallProcessorRetrofit2: CallProcessorRetrofit2<ErrorMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockCallProcessorRetrofit2.processCallAsync(
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns CompletableDeferred(Either.right(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallProcessorRetrofit2.processCallAwait(
                any() as Class<MessageProvider<ErrorMessage>>,
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns Either.left(errorMessage)

        return this
    }
}
