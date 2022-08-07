package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.net.ktor.CallWrapperKtor
import co.early.fore.net.MessageProvider
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred

class StateBuilder internal constructor(private val mockCallWrapperKtor: CallWrapperKtor<ErrorMessage>) {

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        coEvery {
            mockCallWrapperKtor.processCallAsync(
                any() as suspend () -> List<FruitPojo>
            )
        } returns CompletableDeferred(success(listOf(fruitPojo)))

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallWrapperKtor.processCallAwait(
                any() as Class<MessageProvider<ErrorMessage>>,
                any() as suspend () -> List<FruitPojo>
            )
        } returns fail(errorMessage)

        return this
    }
}
