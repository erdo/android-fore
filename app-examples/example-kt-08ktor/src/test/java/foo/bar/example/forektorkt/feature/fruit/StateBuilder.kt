package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.kt.core.Either
import co.early.fore.kt.net.ktor.CallProcessorKtor
import co.early.fore.net.MessageProvider
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred

/**
 *
 */
class StateBuilder internal constructor(private val mockCallProcessorKtor: CallProcessorKtor<ErrorMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockCallProcessorKtor.processCallAsync(
                any() as suspend () -> List<FruitPojo>
            )
        } returns CompletableDeferred(Either.right(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallProcessorKtor.processCallAwait(
                any() as Class<MessageProvider<ErrorMessage>>,
                any() as suspend () -> List<FruitPojo>
            )
        } returns Either.left(errorMessage)

        return this
    }
}
