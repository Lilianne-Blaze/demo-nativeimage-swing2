package inlinedawt.shared;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ByteArrayUtils {

    public static int DEF_BUF_SIZE = 64 * 1024;

    public static int indexOf(byte[] arrayWhere, byte[] arrayWhat) {
        String stringWhere = new String(arrayWhere, StandardCharsets.US_ASCII);
        String stringWhat = new String(arrayWhat, StandardCharsets.US_ASCII);
        return stringWhere.indexOf(stringWhat);
    }

    public static int indexOf(byte[] arrayWhere, String what) {
        String stringWhere = new String(arrayWhere, StandardCharsets.US_ASCII);
        return stringWhere.indexOf(what);
    }

    public static void fill(byte[] array, int offset, int length, int value) {
        for (int i = 0; i < length; i++) {
            array[offset + i] = (byte) value;
        }
    }

    public static void overwriteString(byte[] array, int offset, String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(stringBytes, 0, array, offset, stringBytes.length);
    }

    public static byte[] readFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis, DEF_BUF_SIZE);
        byte[] allBytes = readAllBytes(bis);
        bis.close();
        return allBytes;
    }

    public static void writeFile(String filename, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos, DEF_BUF_SIZE);
        bos.write(bytes);
        bos.flush();
        bos.close();
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null)
                inputStream.close();
            else
                try {
                    inputStream.close();
                } catch (IOException e) {
                    exception.addSuppressed(e);
                }
        }
    }

    public static byte[] readNBytes(InputStream is, int n) throws IOException {
        byte[] buffer = new byte[n];
        int bytesRead = 0;
        while (bytesRead < n) {
            int result = is.read(buffer, bytesRead, n - bytesRead);
            if (result == -1) {
                throw new IOException("End of stream reached before reading " + n + " bytes");
            }
            bytesRead += result;
        }
        return buffer;
    }
}
