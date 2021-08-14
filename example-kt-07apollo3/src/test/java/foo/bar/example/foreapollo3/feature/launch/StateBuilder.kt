package foo.bar.example.foreapollo3.feature.launch

import co.early.fore.kt.core.Either
import co.early.fore.kt.net.apollo.CallProcessorApollo
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollo3.message.ErrorMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred

/**
 *
 */
class StateBuilder internal constructor() {

    val mockCallProcessorApollo: CallProcessorApollo<ErrorMessage> = mockk()

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        val mockResponseSuccess: CallProcessorApollo.SuccessResult<LaunchListQuery.Data> = mockk()

        every {
            mockResponseSuccess.data
        } answers { launches }

        coEvery {
            mockCallProcessorApollo.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.right(mockResponseSuccess))

        coEvery {
            mockCallProcessorApollo.processCallAwait<LaunchListQuery.Data>(any())
        } returns Either.right(mockResponseSuccess)

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockCallProcessorApollo.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.left(errorMessage))

        coEvery {
            mockCallProcessorApollo.processCallAwait<LaunchListQuery.Data>(any())
        } returns Either.left(errorMessage)

        return this
    }

}
