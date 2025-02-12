import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath

expect val fileSystem: FileSystem

@Throws(IOException::class)
fun readFileBytes(fileName: String): ByteArray {
    fileSystem.read(fileName.toPath()) {
        return readByteArray()
    }
}

@Throws(IOException::class)
fun readFileString(fileName: String): String {
    fileSystem.read(fileName.toPath()) {
        return readUtf8()
    }
}
