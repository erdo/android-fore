package co.early.fore.kt.core.ui.synctrigger

enum class ResetRule {
    /*
        Trigger is reset after each successful check
     */
    IMMEDIATELY,

    /*
        Trigger is only reset after a successful check, once a subsequent check fails.
        This is the default.
     */
    ONLY_AFTER_REVERSION,

    /*
        Trigger is never reset i.e. it fires once only _per instance_. NB: SyncTriggers usually
        live in Views and are destroyed and recreated on device rotation along with the View,
        which would give you a new instance - although checkLazy() might be enough to prevent
        issues here. You might instead prefer to keep the SyncTrigger in a ViewModel to reduce
        the likely-hood of getting a new instance of the SyncTrigger.
     */
    NEVER
}