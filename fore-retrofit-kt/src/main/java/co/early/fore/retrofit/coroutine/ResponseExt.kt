package co.early.fore.retrofit.coroutine

import retrofit2.Response

/**
 * If this is a successful Response, the extension function calls next() with the result
 *
 * If this is a failed Response, it returns with a new Error Response (of a new type)
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
suspend fun <T, R> Response<T>.carryOn(
        next: suspend (T) -> Response<R>
): Response<R> {
    return body()?.let {
        return next(it)
    } ?: run {
        return Response.error(errorBody()!!, raw())
    }
}
