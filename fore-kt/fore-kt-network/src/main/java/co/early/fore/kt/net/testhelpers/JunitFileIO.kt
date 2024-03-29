package co.early.fore.kt.net.testhelpers

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * To use this in a unit test, you need to put your data files in a folder called "resources"
 * see the unit tests in the example ktor app
 */
object JunitFileIO {
    @Throws(IOException::class)
    @JvmStatic fun getRawResourceAsUTF8String(resourceFileName: String, classLoader: ClassLoader): String {
        return String(getResourceBytes(resourceFileName, classLoader), Charset.forName("UTF-8"))
    }

    @Throws(IOException::class)
    private fun getResourceBytes(resourceFileName: String, classLoader: ClassLoader): ByteArray {
        return getBytesFromInputStream(classLoader.getResourceAsStream(resourceFileName))
    }

    @Throws(IOException::class)
    private fun getBytesFromInputStream(inputStream: InputStream): ByteArray {
        val bout = ByteArrayOutputStream()
        val readBuffer = ByteArray(4 * 1024)
        return inputStream.use { inpS ->
            var read: Int
            do {
                read = inpS.read(readBuffer, 0, readBuffer.size)
                if (read == -1) {
                    break
                }
                bout.write(readBuffer, 0, read)
            } while (true)
            bout.toByteArray()
        }
    }
}
