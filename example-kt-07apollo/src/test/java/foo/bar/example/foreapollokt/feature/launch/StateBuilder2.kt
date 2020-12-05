package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.kt.Either
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 *
 */
class StateBuilder2 internal constructor() {

    val mockApolloCallProcessor: ApolloCallProcessor<ErrorMessage> = mockk()

    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder2 {

        val mockResponseSuccess: ApolloCallProcessor.SuccessResult<LaunchListQuery.Data> = mockk()

        every {
            mockResponseSuccess.data
        } answers { launches }

        coEvery {
            mockApolloCallProcessor.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.right(mockResponseSuccess))

        return this
    }

    internal fun getLaunchFail(errorMessage: ErrorMessage): StateBuilder2 {

        coEvery {
            mockApolloCallProcessor.processCallAsync<LaunchListQuery.Data>(any())
        } returns CompletableDeferred(Either.left(errorMessage))

        return this
    }

}
