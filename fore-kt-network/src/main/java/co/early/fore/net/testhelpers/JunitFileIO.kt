package co.early.fore.net.testhelpers

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * To use this in a unit test, you need to put your data files in a folder called "resources"
 * see the unit tests for example retrofit app: "fore 4 retrofit ex"
 */
object JunitFileIO {
    @Throws(IOException::class)
    fun getRawResourceAsUTF8String(resourceFileName: String, classLoader: ClassLoader): String {
        return String(getResourceBytes(resourceFileName, classLoader), Charset.forName("UTF-8"))
    }

    @Throws(IOException::class)
    private fun getResourceBytes(resourceFileName: String, classLoader: ClassLoader): ByteArray {
        return getBytesFromInputStream(classLoader.getResourceAsStream(resourceFileName))
    }

    @Throws(IOException::class)
    private fun getBytesFromInputStream(`is`: InputStream): ByteArray {
        val bout = ByteArrayOutputStream()
        val readBuffer = ByteArray(4 * 1024)
        return try {
            var read: Int
            do {
                read = `is`.read(readBuffer, 0, readBuffer.size)
                if (read == -1) {
                    break
                }
                bout.write(readBuffer, 0, read)
            } while (true)
            bout.toByteArray()
        } finally {
            `is`.close()
        }
    }
}