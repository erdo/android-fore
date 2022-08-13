package co.early.fore.adapters.immutable

import co.early.fore.core.time.SystemTimeWrapper
import androidx.recyclerview.widget.DiffUtil.DiffResult

/**
 * Wraps a DiffResult with a timestamp so that we can abandon it when it gets old. This helps
 * the NotifyableImp manage adapter updates appropriately
 *
 * @param diffResult can be null to indicate no changes
 * @param systemTimeWrapper wrapper for the system time
 */
class DiffSpec(val diffResult: DiffResult?, systemTimeWrapper: SystemTimeWrapper) {
    val timeStamp: Long = systemTimeWrapper.currentTimeMillis()
}
