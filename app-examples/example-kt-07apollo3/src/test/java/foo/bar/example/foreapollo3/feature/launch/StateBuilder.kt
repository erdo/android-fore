package foo.bar.example.foreapollo3.feature.launch

import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.net.apollo3.CallWrapperApollo3
import foo.bar.example.foreapollo3.LaunchListQuery
import foo.bar.example.foreapollo3.message.ErrorMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred

/**
 *
 */
@ExperimentalStdlibApi
class StateBuilder internal constructor() {

    val mockCallWrapperApollo: CallWrapperApollo3<ErrorMessage> = mockk()

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        val mockResponseSuccess: CallWrapperApollo3.SuccessResult<LaunchListQuery.Data, ErrorMessage> = mockk()

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
