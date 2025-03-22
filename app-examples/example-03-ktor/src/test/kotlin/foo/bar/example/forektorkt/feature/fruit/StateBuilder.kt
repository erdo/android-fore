package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.core.type.Either.Companion.fail
import co.early.fore.core.type.Either.Companion.success
import co.early.fore.net.wrap.CallWrapper
import co.early.fore.net.MessageProvider
import co.early.fore.net.wrap.FakeCallWrapper
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred

class StateBuilder {

    lateinit var fakeCallWrapper: FakeCallWrapper<ErrorMessage>

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        fakeCallWrapper = FakeCallWrapper<ErrorMessage>(
            listOf(fruitPojo).toFakeSuccesss()
        )

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        fakeCallWrapper = FakeCallWrapper<ErrorMessage>(
            errorMessage.toFakeFail()
        )

        return this
    }
}
