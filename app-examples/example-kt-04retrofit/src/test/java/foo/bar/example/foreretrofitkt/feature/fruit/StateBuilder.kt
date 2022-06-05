package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.net.retrofit2.CallWrapperRetrofit2
import co.early.fore.net.MessageProvider
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response

/**
 *
 */
class StateBuilder internal constructor(private val mockCallWrapperRetrofit2: CallWrapperRetrofit2<ErrorMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockCallWrapperRetrofit2.processCallAsync(
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns CompletableDeferred(success(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallWrapperRetrofit2.processCallAwait(
                any() as Class<MessageProvider<ErrorMessage>>,
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns fail(errorMessage)

        return this
    }
}
