package foo.bar.example.foreapollo.feature.launch

import co.early.fore.net.apollo.CallWrapperApollo.SuccessResult
import co.early.fore.net.wrap.apollo.FakeCallWrapperApollo
import co.early.fore.net.wrap.toFakeFail
import co.early.fore.net.wrap.toFakeSuccess
import foo.bar.example.foreapollo.LaunchListQuery
import foo.bar.example.foreapollo.message.ErrorMessage

class StateBuilder {

    lateinit var fakeCallWrapperApollo: FakeCallWrapperApollo<ErrorMessage>

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        fakeCallWrapperApollo = FakeCallWrapperApollo(
            SuccessResult<LaunchListQuery.Data, ErrorMessage>(launches).toFakeSuccess()
        )

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder {

        fakeCallWrapperApollo = FakeCallWrapperApollo(
            errorMessage.toFakeFail()
        )

        return this
    }
}
