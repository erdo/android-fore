package co.early.fore.kt.core.type

sealed class Either<out F, out S> {

    data class Fail<out F> internal constructor(val value: F) : Either<F, Nothing>() {
        companion object {
            operator fun <F> invoke(f: F): Either<F, Nothing> = Fail(f)
        }
    }

    data class Success<out S> internal constructor(val value: S) : Either<Nothing, S>() {
        companion object {
            operator fun <S> invoke(s: S): Either<Nothing, S> = Success(s)
        }
    }

    companion object {
        fun <S> success(value: S): Either<Nothing, S> = Success(value)
        fun <F> fail(value: F): Either<F, Nothing> = Fail(value)
    }
}

/**
 * If this is a Success, the extension function calls next() with the success value
 * If this is a Fail, it returns the Fail
 *
 * Copyright © 2020 early.co. All rights reserved.
 */
suspend fun <E, S, S2> Either<E, S>.carryOn(
        nextBlock: suspend (S) -> Either<E, S2>
): Either<E, S2> {
    return when (this) {
        is Either.Fail -> this
        is Either.Success -> nextBlock(value)
    }
}
