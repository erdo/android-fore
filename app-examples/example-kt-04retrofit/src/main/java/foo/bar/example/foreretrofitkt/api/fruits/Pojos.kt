package foo.bar.example.foreretrofitkt.api.fruits

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
data class FruitPojo(var name: String, var isCitrus: Boolean, var tastyPercentScore: Int)

/**
 * various pojos for call chain to demonstrate carryOn...
 */
data class UserPojo(val userId: Int)
data class TicketPojo(val ticketRef: String)
data class TicketResultPojo(val ticketRef: String, val completed: Boolean)
data class TimePojo(val minutesWait: Int)
