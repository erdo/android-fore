package foo.bar.example.foreapollo.feature.launch

import co.early.fore.net.apollo.CallWrapperApollo.SuccessResult
import co.early.fore.net.apollo.FakeCallWrapperApollo
import co.early.fore.net.apollo.toApolloFail
import co.early.fore.net.apollo.toApolloSuccess
import foo.bar.example.foreapollo.LaunchListQuery
import foo.bar.example.foreapollo.message.ErrorMessage

class StateBuilder internal constructor() {

    lateinit var fakeCallWrapperApollo: FakeCallWrapperApollo<ErrorMessage>

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        fakeCallWrapperApollo = FakeCallWrapperApollo(
            SuccessResult<LaunchListQuery.Data, ErrorMessage>(launches).toApolloSuccess()
        )

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder {

        fakeCallWrapperApollo = FakeCallWrapperApollo(
            errorMessage.toApolloFail()
        )

        return this
    }
}
