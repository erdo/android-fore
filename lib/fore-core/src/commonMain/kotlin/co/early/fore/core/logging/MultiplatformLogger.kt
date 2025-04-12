package co.early.fore.core.logging

typealias AndroidLogger = MultiplatformLogger

class MultiplatformLogger(
    private val tagPrefix: String? = null,
    private val stableTagLength: Boolean = false,
    overrideMaxTagLength: Int? = null
) : Logger, TagFormatter by TagFormatterImp(overrideMaxTagLength) {

    // this is for iOS target benefit which doesn't like default parameters in constructors
    constructor(tagPrefix: String) : this(tagPrefix, false, null)
    constructor() : this(null, false, null)

    private val tagInferer: TagInferer = getTagInferer()

    override fun e(message: String) {
        e(tagInferer.inferTag(), message)
    }

    override fun w(message: String) {
        w(tagInferer.inferTag(), message)
    }

    override fun i(message: String) {
        i(tagInferer.inferTag(), message)
    }

    override fun d(message: String) {
        d(tagInferer.inferTag(), message)
    }

    override fun v(message: String) {
        v(tagInferer.inferTag(), message)
    }

    override fun e(message: String, throwable: Throwable) {
        e(tagInferer.inferTag(), message, throwable)
    }

    override fun w(message: String, throwable: Throwable) {
        w(tagInferer.inferTag(), message, throwable)
    }

    override fun i(message: String, throwable: Throwable) {
        i(tagInferer.inferTag(), message, throwable)
    }

    override fun d(message: String, throwable: Throwable) {
        d(tagInferer.inferTag(), message, throwable)
    }

    override fun v(message: String, throwable: Throwable) {
        v(tagInferer.inferTag(), message, throwable)
    }

    override fun e(tag: String, message: String) {
        if (stableTagLength) {
            error(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ", message, null)
        } else {
            error(limitTagLength(addTagPrefixIfPresent(tag)), message, null)
        }
    }

    override fun w(tag: String, message: String) {
        if (stableTagLength) {
            warning(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ", message, null)
        } else {
            warning(limitTagLength(addTagPrefixIfPresent(tag)), message, null)
        }
    }

    override fun i(tag: String, message: String) {
        if (stableTagLength) {
            info(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ", message, null)
        } else {
            info(limitTagLength(addTagPrefixIfPresent(tag)), message, null)
        }
    }

    override fun d(tag: String, message: String) {
        if (stableTagLength) {
            debug(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ", message, null)
        } else {
            debug(limitTagLength(addTagPrefixIfPresent(tag)), message, null)
        }
    }

    override fun v(tag: String, message: String) {
        if (stableTagLength) {
            verbose(padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ", message, null)
        } else {
            verbose(limitTagLength(addTagPrefixIfPresent(tag)), message, null)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            error(
                padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ",
                message,
                throwable
            )
        } else {
            error(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            warning(
                padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ",
                message,
                throwable
            )
        } else {
            warning(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            info(
                padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ",
                message,
                throwable
            )
        } else {
            info(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            debug(
                padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ",
                message,
                throwable
            )
        } else {
            debug(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
        if (stableTagLength) {
            verbose(
                padTagWithSpace(limitTagLength(addTagPrefixIfPresent(tag))) + " ",
                message,
                throwable
            )
        } else {
            verbose(limitTagLength(addTagPrefixIfPresent(tag)), message, throwable)
        }
    }

    private fun addTagPrefixIfPresent(message: String): String {
        return tagPrefix?.let {
            it + message
        } ?: message
    }
}
