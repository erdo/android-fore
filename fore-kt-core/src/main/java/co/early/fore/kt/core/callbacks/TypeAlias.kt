package co.early.fore.kt.core.callbacks

@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias Success = () -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias SuccessWithPayload<T> = (T) -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias Failure = () -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias FailureWithPayload<T> = (T) -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias Continue = () -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias ContinueWithPayload<T> = (T) -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias DoThis = () -> Unit
@Deprecated("this isn't generic enough to be widely useful, so will be removed - you can always add these type aliases directly to your project")
typealias DoThisWithPayload<T> = (T) -> Unit
