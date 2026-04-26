import MockLogger.Level.V
import MockLogger.Level.D
import MockLogger.Level.I
import MockLogger.Level.W
import MockLogger.Level.E
import co.early.fore.core.logging.Logger

class MockLogger : Logger {

    private val _callHistory: MutableList<Call> = mutableListOf()
    val callHistory: List<Call> = _callHistory

    class Call(
        val level: Level,
        val tag: String? = null,
        val message: String? = null,
        val throwable: Throwable? = null
    )

    enum class Level {
        V,
        D,
        I,
        W,
        E
    }

    fun clear(){
        _callHistory.clear()
    }

    override fun d(message: String) {
        _callHistory.add(Call(D, message = message))
    }

    override fun d(tag: String, message: String) {
        _callHistory.add(Call(D, tag = tag, message = message))
    }

    override fun d(message: String, throwable: Throwable) {
        _callHistory.add(Call(D, message = message, throwable = throwable))
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
        _callHistory.add(Call(D, tag = tag, message = message, throwable = throwable))
    }

    override fun e(message: String) {
        _callHistory.add(Call(E, message = message))
    }

    override fun e(tag: String, message: String) {
        _callHistory.add(Call(E, tag = tag, message = message))
    }

    override fun e(message: String, throwable: Throwable) {
        _callHistory.add(Call(E, message = message, throwable = throwable))
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        _callHistory.add(Call(E, tag = tag, message = message, throwable = throwable))
    }

    override fun i(message: String) {
        _callHistory.add(Call(I, message = message))
    }

    override fun i(tag: String, message: String) {
        _callHistory.add(Call(I, tag = tag, message = message))
    }

    override fun i(message: String, throwable: Throwable) {
        _callHistory.add(Call(I, message = message, throwable = throwable))
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
        _callHistory.add(Call(I, tag = tag, message = message, throwable = throwable))
    }

    override fun v(message: String) {
        _callHistory.add(Call(V, message = message))
    }

    override fun v(tag: String, message: String) {
        _callHistory.add(Call(V, tag = tag, message = message))
    }

    override fun v(message: String, throwable: Throwable) {
        _callHistory.add(Call(V, message = message, throwable = throwable))
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
        _callHistory.add(Call(V, tag = tag, message = message, throwable = throwable))
    }

    override fun w(message: String) {
        _callHistory.add(Call(W, message = message))
    }

    override fun w(tag: String, message: String) {
        _callHistory.add(Call(W, tag = tag, message = message))
    }

    override fun w(message: String, throwable: Throwable) {
        _callHistory.add(Call(W, message = message, throwable = throwable))
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        _callHistory.add(Call(W, tag = tag, message = message, throwable = throwable))
    }
}
