package co.early.fore.kt.core

sealed class Either<out L, out R> {
    data class Left<out L> internal constructor(val a: L) : Either<L, Nothing>() {
        companion object {
            operator fun <L> invoke(l: L): Either<L, Nothing> = Left(l)
        }
    }

    data class Right<out R> internal constructor(val b: R) : Either<Nothing, R>() {
        companion object {
            operator fun <R> invoke(r: R): Either<Nothing, R> = Right(r)
        }
    }

    companion object {
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
        fun <L> left(value: L): Either<L, Nothing> = Left(value)
    }
}

/**
 * If this is a successful Either (Right), the extension function calls next() with the success
 *
 * If this is a failed Either (Left), it returns the either
 *
 * Copyright © 2020 early.co. All rights reserved.
 */
suspend fun <E, R, R2> Either<E, R>.carryOn(
        nextBlock: suspend (R) -> Either<E, R2>
): Either<E, R2> {
    return when (this) {
        is Either.Left -> Either.left(this.a)
        is Either.Right -> nextBlock(this.b)
    }
}