package co.early.fore.adapters.mutable

import co.early.fore.core.time.SystemTimeWrapper
import java.lang.NullPointerException

/**
 * Indicates what was the most recent change in a list, helps the ChangeAware* classes
 * call the correct notify* method for android adapters to take advantage of built in
 * list animations
 */
class UpdateSpec(
    type: UpdateType,
    rowPosition: Int,
    rowsEffected: Int,
    systemTimeWrapper: SystemTimeWrapper
) {
    enum class UpdateType {
        FULL_UPDATE, ITEM_CHANGED, ITEM_INSERTED, ITEM_REMOVED
    }

    val type: UpdateType
    val rowPosition: Int
    val rowsEffected: Int
    val timeStamp: Long
    private fun <T> notNull(param: T?): T {
        if (param == null) {
            throw NullPointerException("Parameter must not be null")
        }
        return param
    }

    init {
        this.type = notNull(type)
        this.rowPosition = rowPosition
        this.rowsEffected = rowsEffected
        timeStamp = notNull(systemTimeWrapper).currentTimeMillis()
    }
}
