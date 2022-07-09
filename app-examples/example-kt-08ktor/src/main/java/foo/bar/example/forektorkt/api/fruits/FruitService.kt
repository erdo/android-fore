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
data class FruitService(
    val getFruitsSimulateOk: suspend () -> List<FruitPojo>,
    val getFruitsSimulateNotAuthorised: suspend () -> List<FruitPojo>,

    // call chain to demonstrate carryOn
    val createUser: suspend () -> UserPojo,
    val createUserTicket: suspend (Int) -> TicketPojo,
    val getEstimatedWaitingTime: suspend (String) -> TimePojo,
    val cancelTicket: suspend (String) -> TicketResultPojo,
    val confirmTicket: suspend (String) -> TicketResultPojo,
    val claimFreeFruit: suspend (String) -> List<FruitPojo>
) {

    companion object {

        fun create(httpClient: HttpClient): FruitService {

            val baseUrl = "http://www.mocky.io/v2/"
            val mediumDelay = 3
            val smallDelay = 1

            return FruitService(
                    getFruitsSimulateOk = { httpClient.get("${baseUrl}59efa0132e0000ef331c5f9b/?mocky-delay=${mediumDelay}s").body() },
                    getFruitsSimulateNotAuthorised = { httpClient.get("${baseUrl}59ef2a6c2e00002a1a1c5dea/?mocky-delay=${mediumDelay}s").body() },
                    createUser = { httpClient.get("${baseUrl}5de410e83000002b009f78f8/?mocky-delay=${smallDelay}s").body() },
                    createUserTicket = { userId -> httpClient.get("${baseUrl}5de4112e3000002b009f78f9/?userId=${userId}&mocky-delay=${smallDelay}s").body() },
                    getEstimatedWaitingTime = { ticketRef -> httpClient.get("${baseUrl}5de4114830000062009f78fa/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body() },
                    cancelTicket = { ticketRef -> httpClient.get("${baseUrl}5de411c63000002b009f78fc/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body() },
                    confirmTicket = { ticketRef -> httpClient.get("${baseUrl}5de411af3000000e009f78fb/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body() },
                    claimFreeFruit = { ticketRef -> httpClient.get("${baseUrl}59efa0132e0000ef331c5f9b/?ticketRef=${ticketRef}&mocky-delay=${smallDelay}s").body() }
            )
        }
    }
}
