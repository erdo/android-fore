package co.early.fore.apollo.testhelpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * To use this in a unit test, you need to put your data files in a folder called "resources"
 * see the unit tests for example retrofit app: "fore 4 retrofit ex"
 */
public class JunitFileIO {

    public static String getRawResourceAsUTF8String(String resourceFileName, ClassLoader classLoader) throws IOException {
        return new String(getResourceBytes(resourceFileName, classLoader), Charset.forName("UTF-8"));
    }

    private static byte[] getResourceBytes(String resourceFileName, ClassLoader classLoader) throws IOException {
        return getBytesFromInputStream(classLoader.getResourceAsStream(resourceFileName));
    }

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        byte[] readBuffer = new byte[4 * 1024];

        try {
            int read;
            do {
                read = is.read(readBuffer, 0, readBuffer.length);
                if (read == -1) {
                    break;
                }
                bout.write(readBuffer, 0, read);
            } while (true);

            return bout.toByteArray();

        } finally {
            is.close();
        }
    }
}
