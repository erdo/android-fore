import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.SYSTEM

@Throws(IOException::class)
fun readFileBytes(fileName: String): ByteArray {
    FileSystem.SYSTEM.read(fileName.toPath()) {
        return readByteArray()
    }
}

@Throws(IOException::class)
fun readFileString(fileName: String): String {
    FileSystem.SYSTEM.read(fileName.toPath()) {
        return readUtf8()
    }
}
