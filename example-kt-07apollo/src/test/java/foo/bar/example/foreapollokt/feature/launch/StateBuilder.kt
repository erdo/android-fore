package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.kt.core.Either
import co.early.fore.kt.net.apollo.ApolloCallProcessor
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

    val mockApolloCallProcessor: ApolloCallProcessor<ErrorMessage> = mockk()

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {

        val mockResponseSuccess: ApolloCallProcessor.SuccessResult<LaunchListQuery.Data> = mockk()

        every {
            mockResponseSuccess.data
        } answers { launches }

        coEvery {
            mockApolloCallProcessor.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.right(mockResponseSuccess))

        coEvery {
            mockApolloCallProcessor.processCallAwait<LaunchListQuery.Data>(any())
        } returns Either.right(mockResponseSuccess)

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder {

        coEvery {
            mockApolloCallProcessor.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.left(errorMessage))

        coEvery {
            mockApolloCallProcessor.processCallAwait<LaunchListQuery.Data>(any())
        } returns Either.left(errorMessage)

        return this
    }

}
