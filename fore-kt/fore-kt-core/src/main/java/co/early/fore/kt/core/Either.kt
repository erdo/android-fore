package co.early.fore.kt.core

@Deprecated(message = "use Either.Fail instead from the co.early.fore.kt.core.type package", replaceWith = ReplaceWith(expression = "co.early.fore.kt.core.type.Either.Fail", "co.early.fore.kt.core.type.Either.Fail"))
typealias Error<E> = Either.Left<E>
@Deprecated(message = "use Either.Success instead from the co.early.fore.kt.core.type package", replaceWith = ReplaceWith(expression = "co.early.fore.kt.core.type.Either.Success", "co.early.fore.kt.core.type.Either.Success"))
typealias Success<R> = Either.Right<R>

@Deprecated(message = "old Eithers will be removed in a futureversion of fore, use success() instead", replaceWith = ReplaceWith(expression = "success(value)", "co.early.fore.kt.core.type.Either.Companion.success"))
fun <R> eitherSuccess(value: R): Either<Nothing, R> = Either.right(value)
@Deprecated(message = "old Eithers will be removed in a future version of fore, use fail() instead", replaceWith = ReplaceWith(expression = "fail(value)", "co.early.fore.kt.core.type.Either.Companion.fail"))
fun <L> eitherError(value: L): Either<L, Nothing> = Either.left(value)

@Deprecated(message = "this Either will be removed and replaced with co.early.fore.kt.core.type.Either", replaceWith = ReplaceWith(expression = "Either<F, S>", "co.early.fore.kt.core.type.Either"))
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
        @Deprecated(message = "old Eithers will be removed in a futureversion of fore, use success() instead", replaceWith = ReplaceWith(expression = "success(value)", "co.early.fore.kt.core.type.Either.Companion.success"))
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
        @Deprecated(message = "old Eithers will be removed in a futureversion of fore, use fail() instead", replaceWith = ReplaceWith(expression = "fail(value)", "co.early.fore.kt.core.type.Either.Companion.fail"))
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
@Deprecated(message = "this Either will be removed in a future version of fore, please use co.early.fore.kt.core.type.Either", replaceWith = ReplaceWith(expression = "carryOn(nextBlock)", "co.early.fore.kt.core.type.carryOn"))
suspend fun <E, R, R2> Either<E, R>.carryOn(
        nextBlock: suspend (R) -> Either<E, R2>
): Either<E, R2> {
    return when (this) {
        is Either.Left -> Either.left(this.a)
        is Either.Right -> nextBlock(this.b)
    }
}

@Deprecated(message = "legacy Either will be removed in a future version of fore")
fun <F, S> co.early.fore.kt.core.type.Either<F,S>.toLegacyEither(): Either<F,S> {
    return when (this){
        is co.early.fore.kt.core.type.Either.Fail -> Either.left(value)
        is co.early.fore.kt.core.type.Either.Success -> Either.right(value)
    }
}
