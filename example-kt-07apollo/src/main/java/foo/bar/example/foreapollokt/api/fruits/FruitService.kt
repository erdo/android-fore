package foo.bar.example.foreapollokt.api.fruits

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * http://www.mocky.io/v2/59efa0132e0000ef331c5f9b
 * http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 *
 */
interface FruitService {

    @GET("59efa0132e0000ef331c5f9b/")
    suspend fun getFruitsSimulateOk(@Query("mocky-delay") delay: String = "3s"): Response<List<FruitPojo>>

    @GET("59ef2a6c2e00002a1a1c5dea/")
    suspend fun getFruitsSimulateNotAuthorised(@Query("mocky-delay") delay: String = "3s"): Response<List<FruitPojo>>

    // call chain to demonstrate carryOn

    @GET("5de410e83000002b009f78f8/")
    suspend fun createUser(): Response<UserPojo>

    @GET("5de4112e3000002b009f78f9/")
    suspend fun createUserTicket(@Query("userId") userId: Int, @Query("mocky-delay") delay: String = "1s"): Response<TicketPojo>

    @GET("5de4114830000062009f78fa/")
    suspend fun getEstimatedWaitingTime(@Query("ticketRef") ticketRef: String, @Query("mocky-delay") delay: String = "1s"): Response<TimePojo>

    @GET("5de411c63000002b009f78fc/")
    suspend fun cancelTicket(@Query("ticketRef") ticketRef: String, @Query("mocky-delay") delay: String = "1s"): Response<TicketResultPojo>

    @GET("5de411af3000000e009f78fb/")
    suspend fun confirmTicket(@Query("ticketRef") ticketRef: String, @Query("mocky-delay") delay: String = "1s"): Response<TicketResultPojo>

    @GET("59efa0132e0000ef331c5f9b/")
    suspend fun claimFreeFruit(@Query("ticketRef") ticketRef: String, @Query("mocky-delay") delay: String = "1s"): Response<List<FruitPojo>>

}
