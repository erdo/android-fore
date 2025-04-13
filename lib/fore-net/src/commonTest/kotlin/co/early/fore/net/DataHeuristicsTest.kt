package co.early.fore.net

import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.TestDelegateDefault
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Buffer
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.BeforeTest

class DataHeuristicsTest {

    @BeforeTest
    fun setup(){
        Fore.setDelegate(TestDelegateDefault())
    }

    @Test
    fun testJsonFormatDetection() {
        val body = Buffer().writeUtf8("{\"key\":\"value\"}")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Json, result)
    }

    @Test
    fun testJsonFormatDetection2() {
        val body = Buffer().writeUtf8("   [1, 2, 3]")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Json, result)
    }

    @Test
    fun testJsonFormatDetection3() {
        val body = Buffer().writeUtf8(
            "{" +
                    "   \"key\":\"value\"" +
                    "}"
        )
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Json, result)
    }

    @Test
    fun testXmlFormatDetection() {
        val body = Buffer().writeUtf8("<?xml version=\"1.0\"?><root></root>")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Xml, result)
    }

    @Test
    fun testHtmlFormatDetection() {
        val body = Buffer().writeUtf8("<html><body>Hello</body></html>")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Html, result)
    }

    @Test
    fun testJsFileFormatDetection() {
        val body = Buffer().writeUtf8(readFileString("src/commonTest/resources/jquery.js"))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testPlainTextFormatDetection() {
        val body = Buffer().writeUtf8("Just some plain text.")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testPlainAsciiTextFormatDetection() {
        val body = Buffer().write(byteArrayOf(0x68, 0x65, 0x6C, 0x6C, 0x6F)) // h e l l o in ASCII
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testPolishTextDetection3() {
        val body = Buffer().writeUtf8(readFileString("src/commonTest/resources/polish.txt"))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testBinaryDataDetection() {
        // PNG file
        val body = Buffer().write(byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(), // PNG Signature
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0D.toByte(), // IHDR Chunk Length
            0x49.toByte(), 0x48.toByte(), 0x44.toByte(), 0x52.toByte(), // IHDR Chunk Type
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), // Width: 1
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), // Height: 1
            0x08.toByte(), 0x06.toByte(), 0x00.toByte(), 0x00.toByte(), // Bit Depth: 8, Color Type: 6 (RGBA)
            0x00.toByte(), 0x1F.toByte(), 0x15.toByte(), 0xC4.toByte(), // CRC for IHDR
            0x89.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), // IDAT Chunk Length
            0x0A.toByte(), 0x49.toByte(), 0x44.toByte(), 0x41.toByte(), // IDAT Chunk Type
            0x54.toByte(), 0x78.toByte(), 0x9C.toByte(), 0x63.toByte(), // Compressed Data
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x02.toByte(), // ZLIB Compressed Block
            0x00.toByte(), 0x01.toByte(), 0xE2.toByte(), 0x21.toByte(), // CRC for IDAT
            0xBC.toByte(), 0xA1.toByte(), 0xE6.toByte(), 0xF3.toByte(), // CRC for IDAT (continued)
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), // IEND Chunk Length
            0x49.toByte(), 0x45.toByte(), 0x4E.toByte(), 0x44.toByte(), // IEND Chunk Type
            0xAE.toByte(), 0x42.toByte(), 0x60.toByte(), 0x82.toByte()  // CRC for IEND
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Binary, result)
    }

    @Test
    fun testJpgBinaryDetection() {
        val body = Buffer().writeUtf8(readFileString("src/commonTest/resources/test.jpg"))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Binary, result)
    }

    @Test
    fun testUTF16BigEndianPlainTextDetection() {
        val body = Buffer().write(byteArrayOf(
            // "Привет, мир!"
            0x04.toByte(), 0x1F.toByte(), // 'П'
            0x04.toByte(), 0x40.toByte(), // 'р'
            0x04.toByte(), 0x38.toByte(), // 'и'
            0x04.toByte(), 0x32.toByte(), // 'в'
            0x04.toByte(), 0x35.toByte(), // 'е'
            0x04.toByte(), 0x42.toByte(), // 'т'
            0x00.toByte(), 0x2C.toByte(), // ','
            0x00.toByte(), 0x20.toByte(), // ' '
            0x04.toByte(), 0x3C.toByte(), // 'м'
            0x04.toByte(), 0x38.toByte(), // 'и'
            0x04.toByte(), 0x40.toByte(), // 'р'
            0x00.toByte(), 0x21.toByte()  // '!'
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testUTF32BigEndianPlainTextDetection() {
        val body = Buffer().write(byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x48.toByte(), // 'H'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x65.toByte(), // 'e'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x6C.toByte(), // 'l'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x6C.toByte(), // 'l'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x6F.toByte(), // 'o'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x2C.toByte(), // ','
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x20.toByte(), // ' '
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x77.toByte(), // 'w'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x6F.toByte(), // 'o'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x72.toByte(), // 'r'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x6C.toByte(), // 'l'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x64.toByte(), // 'd'
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x21.toByte(), // '!'
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testUTF32LittleEndianPlainTextDetection() {
        val body = Buffer().write(byteArrayOf(
            0x1F.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'П'
            0x40.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'р'
            0x38.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'и'
            0x32.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'в'
            0x35.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'е'
            0x42.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'т'
            0x2C.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), // ','
            0x20.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), // ' '
            0x3C.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'м'
            0x38.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'и'
            0x40.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), // 'р'
            0x21.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()  // '!'
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testBadlyTruncatedUTF8() {

        val validUtf8With4ByteFinalCharacter = "\uD83E\uDD23\uD83D\uDCA9\uD83D\uDE0E\uFE0F\uD83D\uDE40\uD83D\uDE0D\uD83D\uDE2C\uD83D\uDC80"  // "🤣💩😎️🙀😍😬💀"

        val body = Buffer().write(
            validUtf8With4ByteFinalCharacter.toByteArray().dropLast(1).toByteArray()  // truncate 1 byte only
        )

        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testShiftJsPlainTextDetection() {
        val body = Buffer().write(byteArrayOf(
            // "こんにちは" (Hello)
            0x82.toByte(), 0xA0.toByte(), // こ
            0x82.toByte(), 0xA2.toByte(), // ん
            0x82.toByte(), 0xA4.toByte(), // に
            0x82.toByte(), 0xA6.toByte(), // ち
            0x82.toByte(), 0xA8.toByte(), // は

            // "世界" (World)
            0x8C.toByte(), 0xF3.toByte(), // 世
            0x8C.toByte(), 0xF1.toByte(), // 界
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testBig5PlainTextDetection() {
        val body = Buffer().write(byteArrayOf(
            0xA4.toByte(), 0xA4.toByte(),  // "中"
            0xA4.toByte(), 0xA6.toByte(),  // "文"
            0xA4.toByte(), 0xAB.toByte(),  // "国"
            0xA5.toByte(), 0xB7.toByte(),  // "人"
            0xA5.toByte(), 0xB6.toByte(),  // "大"
            0xA4.toByte(), 0xAD.toByte(),  // "学"
            0xA6.toByte(), 0xA1.toByte(),  // "生"
            0xA5.toByte(), 0xD5.toByte(),  // "好"
            0xA4.toByte(), 0xAE.toByte(),  // "天"
            0xA6.toByte(), 0xA2.toByte(),  // "地"
            0xA1.toByte(), 0xE4.toByte(),  // "的"
            0xA1.toByte(), 0xC2.toByte(),  // "是"
            0xA4.toByte(), 0xE4.toByte(),  // "文"
            0xA9.toByte(), 0xAA.toByte(),  // "？"
            0xA9.toByte(), 0xA8.toByte()   // "！"
        ))
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testJsonWithLeadingWhiteSpaceFormatDetection() {
        val body = Buffer().writeUtf8("   \n{\"key\":\"value\"}")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.Json, result)
    }

    @Test
    fun testVeryShortBuffer() {
        val shortBuffer = Buffer().writeUtf8("a")
        assertEquals(BodyRenderFormat.PlainText, inferBodyRenderFormat(shortBuffer))
    }

    @Test
    fun testWhitespaceOnlyBuffer() {
        val body = Buffer().writeUtf8("   \n\t   ")
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }

    @Test
    fun testEmptyBufferDetection() {
        val body = Buffer()
        val result = inferBodyRenderFormat(body)
        assertEquals(BodyRenderFormat.PlainText, result)
    }
}

fun readFileString(path: String): String {
    return okio.FileSystem.SYSTEM.read(path.toPath()) { readUtf8() }
}
