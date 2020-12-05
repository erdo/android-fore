package co.early.fore.kt

sealed class Either<out L, out R> {
    data class Left<out L> internal constructor (val a: L): Either<L, Nothing>() {
        companion object {
            operator fun <L> invoke(l: L): Either<L, Nothing> = Left(l)
        }
    }
    data class Right<out R> internal constructor (val b: R): Either<Nothing, R>() {
        companion object {
            operator fun <R> invoke(r: R): Either<Nothing, R> = Right(r)
        }
    }

    companion object {
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
        fun <L> left(value: L): Either<L, Nothing> = Left(value)
    }
}