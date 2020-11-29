package co.early.fore.kt.core.logging

import android.util.Log

class AndroidLogger(private val tagPrefix: String? = null,
                    private val stableTagLength: Boolean = false,
                    overrideMaxTagLength: Int? = null) :
        Logger,
        TagInferer by TagInfererImpl(),
        TagFormatter by TagFormatterImpl(overrideMaxTagLength) {

    override fun e(message: String) {
        e(inferTag(), message)
    }

    override fun w(message: String) {
        w(inferTag(), message)
    }

    override fun i(message: String) {
        i(inferTag(), message)
    }

    override fun d(message: String) {
        d(inferTag(), message)
    }

    override fun v(message: String) {
        v(inferTag(), message)
    }

    override fun e(message: String, throwable: Throwable) {
        e(inferTag(), message, throwable)
    }

    override fun w(message: String, throwable: Throwable) {
        w(inferTag(), message, throwable)
    }

    override fun i(message: String, throwable: Throwable) {
        i(inferTag(), message, throwable)
    }

    override fun d(message: String, throwable: Throwable) {
        d(inferTag(), message, throwable)
    }

    override fun v(message: String, throwable: Throwable) {
        v(inferTag(), message, throwable)
    }

    override fun e(tag: String, message: String) {
        if (stableTagLength) {
            Log.e(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message)
        } else {
            Log.e(limitTagLength(addTagPrefixIfPresent(tag)), message)
        }
    }

    override fun w(tag: String, message: String) {
        if (stableTagLength) {
            Log.w(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message)
        } else {
            Log.w(limitTagLength(addTagPrefixIfPresent(tag)), message)
        }
    }

    override fun i(tag: String, message: String) {
        if (stableTagLength) {
            Log.i(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message)
        } else {
            Log.i(limitTagLength(addTagPrefixIfPresent(tag)), message)
        }
    }

    override fun d(tag: String, message: String) {
        if (stableTagLength) {
            Log.d(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message)
        } else {
            Log.d(limitTagLength(addTagPrefixIfPresent(tag)), message)
        }
    }

    override fun v(tag: String, message: String) {
        if (stableTagLength) {
            Log.v(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message)
        } else {
            Log.v(limitTagLength(addTagPrefixIfPresent(tag)), message)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            Log.e(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message, throwable)
        } else {
            Log.e(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            Log.w(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message, throwable)
        } else {
            Log.w(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            Log.i(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message, throwable)
        } else {
            Log.i(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            Log.d(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message, throwable)
        } else {
            Log.d(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            Log.e(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + "|", message, throwable)
        } else {
            Log.e(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    private fun addTagPrefixIfPresent(message: String): String {
        return tagPrefix?.let {
            it + message
        } ?: message
    }
}
