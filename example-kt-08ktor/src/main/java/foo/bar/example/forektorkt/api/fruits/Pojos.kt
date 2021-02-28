package foo.bar.example.forektorkt.api.fruits

import kotlinx.serialization.Serializable

/**
 *
 *
 * <Code>
 *
 * The server returns us a list of fruit that look like this:
 *
 * {
 * "name":"papaya",
 * "isCitrus":false,
 * "tastyPercentScore":98
 * }
 *
 * </Code>
 *
 *
 */
@Serializable
data class FruitPojo(var name: String, var isCitrus: Boolean, var tastyPercentScore: Int)

/**
 * various pojos for call chain to demonstrate carryOn...
 */
@Serializable
data class UserPojo(val userId: Int)
@Serializable
data class TicketPojo(val ticketRef: String)
@Serializable
data class TicketResultPojo(val ticketRef: String, val completed: Boolean)
@Serializable
data class TimePojo(val minutesWait: Int)
