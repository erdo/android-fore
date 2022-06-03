package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.net.apollo.CallWrapperApollo
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred

/**
 *
 */
class StateBuilder internal constructor() {

    val mockCallWrapperApollo: CallWrapperApollo<ErrorMessage> = mockk()

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        val mockResponseSuccess: CallWrapperApollo.SuccessResult<LaunchListQuery.Data> = mockk()

        every {
            mockResponseSuccess.data
        } answers { launches }

        coEvery {
            mockCallWrapperApollo.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(success(mockResponseSuccess))

        coEvery {
            mockCallWrapperApollo.processCallAwait<LaunchListQuery.Data>(any())
        } returns success(mockResponseSuccess)

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallWrapperApollo.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(fail(errorMessage))

        coEvery {
            mockCallWrapperApollo.processCallAwait<LaunchListQuery.Data>(any())
        } returns fail(errorMessage)

        return this
    }

}
