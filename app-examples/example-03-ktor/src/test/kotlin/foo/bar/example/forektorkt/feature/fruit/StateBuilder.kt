package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.net.wrap.FakeCallWrapper
import co.early.fore.net.wrap.toFakeFail
import co.early.fore.net.wrap.toFakeSuccess
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.message.ErrorMessage

class StateBuilder {

    lateinit var fakeCallWrapper: FakeCallWrapper<ErrorMessage>

    internal fun getFruitSuccess(fruitPojo: FruitPojo): StateBuilder {

        fakeCallWrapper = FakeCallWrapper(
            listOf(fruitPojo).toFakeSuccess()
        )

        return this
    }

    internal fun getFruitFail(errorMessage: ErrorMessage): StateBuilder {

        fakeCallWrapper = FakeCallWrapper(
            errorMessage.toFakeFail(),
        )

        return this
    }
}
