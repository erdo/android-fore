import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.SYSTEM

@Throws(IOException::class)
fun readFileBytes(fileName: String, fileSystem: FileSystem = FileSystem.SYSTEM): ByteArray {
    fileSystem.read(fileName.toPath()) {
        return readByteArray()
    }
}

@Throws(IOException::class)
fun readFileString(fileName: String, fileSystem: FileSystem = FileSystem.SYSTEM): String {
    fileSystem.read(fileName.toPath()) {
        return readUtf8()
    }
}
