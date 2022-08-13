package co.early.fore.kt.net.testhelpers.okhttp3v3x

import okhttp3.*
import okhttp3.Response.Builder

fun v3xResponse(
    request: Request,
    protocol: String,
    httpCode: Int,
    body: String,
    message: String,
    headers: List<Pair<String, String>>,
): Response {

    val builder = Builder()
        .request(request)
        .protocol(Protocol.get(protocol))
        .code(httpCode)
        .body(ResponseBody.create(null, body))
        .message(message)

    for (header in headers){
        builder.addHeader(header.first, header.second)
    }

    return builder.build()
}