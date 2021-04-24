package co.early.fore.kt.core.delegate

import co.early.fore.core.WorkMode
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.core.logging.Logger

class DebugDelegateDefault (
        tagPrefix: String? = null,
        override val workMode: WorkMode = WorkMode.ASYNCHRONOUS,
        override val logger: Logger = AndroidLogger(tagPrefix),
        override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate
