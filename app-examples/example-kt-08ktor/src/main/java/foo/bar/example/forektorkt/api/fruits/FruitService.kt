package foo.bar.example.forektorkt.api.fruits

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * http://www.mocky.io/v2/59efa0132e0000ef331c5f9b
 *
 * fail example response:
 * http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 */
class FruitService(private val httpClient: HttpClient) {

    private val baseUrl = "http://www.mocky.io/v2/"
    private val mediumDelay = 3
    private val smallDelay = 1

    suspend fun getFruitsSimulateOk(): List<FruitPojo> {
        return httpClient.get("${baseUrl}59efa0132e0000ef331c5f9b/?mocky-delay=${mediumDelay}s").body()
    }

    suspend fun getFruitsSimulateNotAuthorised(): List<FruitPojo> {
        return httpClient.get("${baseUrl}59ef2a6c2e00002a1a1c5dea/?mocky-delay=${mediumDelay}s").body()
    }

    // call chain to demonstrate carryOn

    suspend fun createUser(): UserPojo {
        return httpClient.get("${baseUrl}5de410e83000002b009f78f8/?mocky-delay=${smallDelay}s").body()
    }

    suspend fun createUserTicket(userId: Int): TicketPojo {
        return httpClient.get("${baseUrl}5de4112e3000002b009f78f9/?userId=${userId}&mocky-delay=${smallDelay}s").body()
    }

    suspend fun getEstimatedWaitingTime(ticketRef: String): TimePojo {
        return httpClient.get("${baseUrl}5de4114830000062009f78fa/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body()
    }

    suspend fun cancelTicket(ticketRef: String): TicketResultPojo {
        return httpClient.get("${baseUrl}5de411c63000002b009f78fc/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body()
    }

    suspend fun confirmTicket(ticketRef: String): TicketResultPojo {
        return httpClient.get("${baseUrl}5de411af3000000e009f78fb/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body()
    }

    suspend fun claimFreeFruit(ticketRef: String): List<FruitPojo> {
        return httpClient.get("${baseUrl}59efa0132e0000ef331c5f9b/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body()
    }
}
