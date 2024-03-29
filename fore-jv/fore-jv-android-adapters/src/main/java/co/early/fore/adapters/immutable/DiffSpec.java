package co.early.fore.adapters.immutable;


import androidx.recyclerview.widget.DiffUtil;

import co.early.fore.core.time.SystemTimeWrapper;

/**
 * Wraps a DiffResult with a timestamp so that we can abandon it when it gets old. This helps
 * the NotifyableImp manage adapter updates appropriately
 */
public class DiffSpec {

    public final DiffUtil.DiffResult diffResult;
    public final long timeStamp;

    /**
     * @param diffResult can be null to indicate no changes
     * @param systemTimeWrapper wrapper for the system time (can not be null)
     */
    public DiffSpec(DiffUtil.DiffResult diffResult, SystemTimeWrapper systemTimeWrapper) {
        this.diffResult = diffResult;
        this.timeStamp = notNull(systemTimeWrapper).currentTimeMillis();
    }

    private <T> T notNull(T param) {
        if (param == null) {
            throw new NullPointerException("Parameter must not be null");
        }
        return param;
    }
}
