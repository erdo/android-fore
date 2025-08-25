package co.early.fore.net

import co.early.fore.core.delegate.Fore
import co.early.fore.net.BodyRenderFormat.Binary
import co.early.fore.net.BodyRenderFormat.Html
import co.early.fore.net.BodyRenderFormat.Json
import co.early.fore.net.BodyRenderFormat.PlainText
import co.early.fore.net.BodyRenderFormat.Xml
import co.early.fore.net.EncodingGuess.AsciiIsh
import co.early.fore.net.EncodingGuess.Big5
import co.early.fore.net.EncodingGuess.Shift_Js
import co.early.fore.net.EncodingGuess.Utf16_32
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.content.OutgoingContent.ByteArrayContent
import io.ktor.http.content.OutgoingContent.NoContent
import io.ktor.http.content.OutgoingContent.ProtocolUpgrade
import io.ktor.http.content.OutgoingContent.ReadChannelContent
import io.ktor.http.content.OutgoingContent.WriteChannelContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readFully
import okio.Buffer


internal sealed class BodyRenderFormat {
    data object Json : BodyRenderFormat()
    data object Xml : BodyRenderFormat()
    data object Html : BodyRenderFormat()
    data object PlainText : BodyRenderFormat()
    data object Curl : BodyRenderFormat()
    data object Binary : BodyRenderFormat()
}

internal sealed class EncodingGuess {
    data object Utf8 : EncodingGuess()
    data object Utf16_32 : EncodingGuess()
    data object AsciiIsh : EncodingGuess()
    data object Shift_Js : EncodingGuess()
    data object Big5 : EncodingGuess()
    data object Binary : EncodingGuess()
}

private const val sampleBytes: Long = 64
private const val asciiThreshold = 2f / 3f
private const val utf16Utf32Threshold = 1f / 2f
private const val shiftJsThreshold = 1f / 3f
private const val big5Threshold = 1f / 3f

internal fun inferBodyRenderFormat(body: Buffer): BodyRenderFormat {

    body.utf8TrimStart() // start by assuming utf-8
    if (body.size == 0L || body.exhausted()) {
        return PlainText
    }

    val sample = Buffer()
    body.copyTo(sample, 0, minOf(body.size, sampleBytes))
    val readCodePoint = {
        // don't attempt a read if there are less than 4 bytes remaining
        // as the remaining data could be an incomplete UTF-8 code point and will fail
        if (sample.exhausted() && sample.size < 4) {
            null
        } else sample.readUtf8CodePoint()
    }

    return when (readCodePoint()) {
        '{'.code -> Json
        '['.code -> Json
        '<'.code -> {
            if (readCodePoint() == '?'.code) {
                Xml
            } else Html
        }

        else -> {
            val encodingGuess = guessNonUtf8Encoding(sample)
            Fore.i("Encoding Guess:$encodingGuess")
            when (encodingGuess) {
                EncodingGuess.Binary -> {

                    // double check we are not UTF-8 with multibyte encoding
                    // 0xFFFD would be okio 'fixing' malformed UTF-8 data for us, absence of that
                    // means likely valid UTF-8.

                    // Ignore any final 0xFFFD, because that could have been due to mid character
                    // truncation of a valid 4 byte UTF-8 character
                    val utf8 = body.readUtf8().let {
                        if (it.endsWith(0xFFFD.toChar())) {
                            it.dropLast(1)
                        } else {
                            it
                        }
                    }

                    if (utf8.contains(0xFFFD.toChar())) {
                        Binary
                    } else {
                        PlainText
                    }
                }

                else -> PlainText
            }
        }
    }
}

private fun guessNonUtf8Encoding(sample: Buffer): EncodingGuess {
    var asciiIsh = 0
    var binaryIsh = 0
    var utf1632Ish = 0
    var shiftJSIsh = 0
    var big5Ish = 0
    val peek = sample.peek()
    val sampleSize = sample.size

    while (!peek.exhausted()) {
        val byte = peek.readByte().toInt() and 0xFF // otherwise we get a signed Int back
        when (byte) {
            in 0x20..0x80 -> asciiIsh++ // so likely to be ASCII  / ISO-8859-1 / Windows-1252
            0x00, 0x04 -> utf1632Ish++
            in 0x81..0x83, 0x8C, 0xA1 -> shiftJSIsh++
            in 0xA1..0xA9 -> big5Ish++
            else -> binaryIsh++
        }
    }

//    Fore.i("ASCII LIKE $asciiIsh threshold:${sampleSize * asciiThreshold}")
//    Fore.i("UTF-16-32 $utf1632Ish threshold:${sampleSize * utf16Utf32Threshold}")
//    Fore.i("SHIFT-JS $shiftJSIsh threshold:${sampleSize * shiftJsThreshold}")
//    Fore.i("BIG5 $big5Ish threshold:${sampleSize * big5Threshold}")

    return if (met(asciiIsh, sampleSize, asciiThreshold)) {
        AsciiIsh
    } else if (met(utf1632Ish, sampleSize, utf16Utf32Threshold)) {
        Utf16_32
    } else if (met(shiftJSIsh, sampleSize, shiftJsThreshold)) {
        Shift_Js
    } else if (met(big5Ish, sampleSize, big5Threshold)) {
        Big5
    } else { // other unsupported encodings will get dumped in here
        EncodingGuess.Binary
    }
}

private fun met(count: Int, sampleSize: Long, threshold: Float): Boolean {
    return (count > 0 && count >= (sampleSize * threshold).toInt())
}

private fun Buffer.utf8TrimStart() {
    val bufferedSource = peek()
    var whiteSpaceChars = 0
    while (!bufferedSource.exhausted()) {
        val codePoint = bufferedSource.readUtf8CodePoint()
        if (codePoint.toChar().isWhitespace()) {
            whiteSpaceChars++
        } else {
            break
        }
    }
    repeat(whiteSpaceChars) {
        readUtf8CodePoint() // burn the white space characters
    }
}

internal suspend fun extractBodyInfo(
    body: Any,
    maxBodyLogBytes: Int,
    logEmptyBody: Boolean = true,
): Pair<Buffer, String> {

    var message = ""

    val bodyBuffer = when (body) {
        /**
         * Responses
         */
        is ByteReadChannel -> {

            val buffer = Buffer()

            val copiedChannel = ByteChannel(autoFlush = true)
            var bytesRead: Int
            maxBodyLogBytes.toLong().let {
                bytesRead = body.copyTo(copiedChannel, it + 1).toInt()
                if (bytesRead > it) {
                    message =
                        "[truncated, consider increasing maxBodyLogBytes from:$maxBodyLogBytes]"
                    bytesRead--
                }
            }

            if (bytesRead > 0 || logEmptyBody) {
                val bytes = ByteArray(bytesRead)
                copiedChannel.readFully(bytes, 0, bytesRead)
                buffer.write(bytes)
            }

            buffer
        }
        /**
         * Requests
         */
        is ByteArrayContent -> {
            if (body.bytes().size > maxBodyLogBytes) {
                message =
                    "[truncated, consider increasing maxBodyLogBytes from:$maxBodyLogBytes, e.g try maxBodyLogBytes=BIG_LOG in PluginLogging constructor]"
            }
            val bytes = body.bytes().take(maxBodyLogBytes).toByteArray()
            val buffer = Buffer()
            buffer.write(bytes)
            buffer
        }

        is MultiPartFormDataContent -> {
            message =
                "[multipart form data - boundary:${body.boundary} contentType:${body.contentType}]"
            null
        }

        is ReadChannelContent, is WriteChannelContent, is ProtocolUpgrade -> {
            message = "[body logging unsupported for $body]"
            null
        }

        is String -> { //often how apollo gql will send requests
            val buffer = Buffer()
            buffer.write(body.toByteArray())
            buffer
        }

        is NoContent -> {
            message = if (logEmptyBody) {
                "[no body]"
            } else ""
            null
        }

        else -> {
            message = "[unrecognised body ${body}]"
            null
        }
    }

    return (bodyBuffer ?: Buffer()) to message
}
