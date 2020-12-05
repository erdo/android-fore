package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.kt.net.apollo.ApolloCallProcessor
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

/**
 *
 */
class StateBuilder internal constructor() {

//    private val mockSuccessCall: ApolloQueryCall<LaunchListQuery.Data> = mockk()
//    private val mockFailGenericCall: ApolloQueryCall<LaunchListQuery.Data> = mockk()
//    private val mockFailSpecificCall: ApolloQueryCall<LaunchListQuery.Data> = mockk()
//
//    private val mockCallGetterSuccess: () -> ApolloQueryCall<LaunchListQuery.Data> = mockk()
//    private val mockCallGetterFailGeneric: () -> ApolloQueryCall<LaunchListQuery.Data> = mockk()
//    private val mockCallGetterFailSpecific: () -> ApolloQueryCall<LaunchListQuery.Data> = mockk()
//
//    private val captureCallbackSuccess = slot<ApolloCall.Callback<LaunchListQuery.Data>>()
//    private val captureCallbackFailGeneric = slot<ApolloCall.Callback<LaunchListQuery.Data>>()
//    private val captureCallbackFailSpecific = slot<ApolloCall.Callback<LaunchListQuery.Data>>()
//
//    private val mockResponseSuccess: Response<LaunchListQuery.Data> = mockk()
//    private val mockResponseFailGeneric: Response<LaunchListQuery.Data> = mockk()
//    private val mockResponseFailSpecific: Response<LaunchListQuery.Data> = mockk()
//
//    val launchService = LaunchService(
//            getLaunchList = mockCallGetterSuccess,
//            getLaunchListFailGeneric = mockCallGetterFailGeneric,
//            getLaunchListFailSpecific = mockCallGetterFailSpecific
//    )
//
//    init {
//        every { mockCallGetterSuccess.invoke() } returns mockSuccessCall
//        every { mockCallGetterFailGeneric.invoke() } returns mockFailGenericCall
//        every { mockCallGetterFailSpecific.invoke() } returns mockFailSpecificCall
//
//        every {
//            mockSuccessCall.enqueue(capture(captureCallbackSuccess))
//        } answers { captureCallbackSuccess.captured.onResponse(mockResponseSuccess) }
//
//        every {
//            mockFailGenericCall.enqueue(capture(captureCallbackFailGeneric))
//        } answers { captureCallbackFailGeneric.captured.onResponse(mockResponseFailGeneric) }
//
//        every {
//            mockFailSpecificCall.enqueue(capture(captureCallbackFailSpecific))
//        } answers { captureCallbackFailSpecific.captured.onResponse(mockResponseFailSpecific) }
//    }
//
//
//    internal fun getLaunchSuccess(launches: LaunchListQuery.Data): StateBuilder {
//
//        every {
//            mockResponseSuccess.errors
//        } answers { null }
//
//        every {
//            mockResponseSuccess.data
//        } answers { launches }
//
//        return this
//    }
//
//    internal fun getLaunchFail(errorCode: String): StateBuilder {
//
//        val error = Error(message = "test", customAttributes = mapOf("code" to errorCode))
//
//        every {
//            mockResponseFailGeneric.errors
//        } answers { listOf(error) }
//
//        every {
//            mockResponseFailGeneric.data
//        } answers { null }
//
//
//        every {
//            mockResponseFailSpecific.errors
//        } answers { listOf(error) }
//
//        every {
//            mockResponseFailSpecific.data
//        } answers { null }
//
//
//        return this
//    }

}
