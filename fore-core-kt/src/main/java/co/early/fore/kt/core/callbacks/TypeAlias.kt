package co.early.fore.kt.core.callbacks

typealias Success = () -> Unit
typealias SuccessWithPayload<T> = (T) -> Unit
typealias Failure = () -> Unit
typealias FailureWithPayload<T> = (T) -> Unit
typealias Continue = () -> Unit
typealias ContinueWithPayload<T> = (T) -> Unit
typealias DoThis = () -> Unit
typealias DoThisWithPayload<T> = (T) -> Unit
