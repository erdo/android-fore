import co.early.fore.core.observer.Observer

class MockObserver : Observer {

    private var notifications = 0

    fun notifications(): Int {
        return notifications
    }

    override fun somethingChanged() {
        notifications++
    }
}