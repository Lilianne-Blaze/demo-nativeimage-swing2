package inlinedawt.shared;

import java.nio.charset.StandardCharsets;

public class StringUtils {

    public static byte[] toAscii(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    public static String fromAscii(byte[] buf) {
        return new String(buf, StandardCharsets.US_ASCII);
    }

    // public static byte[] toUtf8(String s) {
    // return s.getBytes(StandardCharsets.UTF_8);
    // }
    //
    // public static String fromUtf8(byte[] buf) {
    // return new String(buf, StandardCharsets.UTF_8);
    // }

}
